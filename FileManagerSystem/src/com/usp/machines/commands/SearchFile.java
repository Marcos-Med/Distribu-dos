package com.usp.machines.commands;

import com.usp.items.FilePeer;
import com.usp.items.TypeMessages;
import com.usp.items.Status;
import com.usp.items.NeighborPeer;
import com.usp.items.StatsManager;
import com.usp.items.StatisticRecord;
import com.usp.machines.InterfacePeer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Comando responsável por listar e buscar arquivos na rede de peers,
 * além de registrar estatísticas de download.
 */
public class SearchFile implements Command {
    private final List<NeighborPeer> peerList;
    private final String directory;

    public SearchFile(List<NeighborPeer> peerList, String directory) {
        this.peerList = peerList;
        this.directory = directory;
    }

    @Override
    public void execute(String[] args) {
        Scanner input = InterfacePeer.getInput();
        Map<Item, Item> catalog = new HashMap<>();

        // 1) Solicita lista de arquivos (LS) de cada peer online
        for (NeighborPeer peer : peerList) {
            if (peer.getStatus() == Status.ONLINE) {
                try {
                    // incrementa relógio e envia LS
                    args[1] = Integer.toString(FilePeer.getInstance().tick());
                    String resp = peer.connect(TypeMessages.LS, args);
                    String[] parts = resp.split(" ");
                    FilePeer.getInstance().tick(Integer.parseInt(parts[1]));

                    int count = Integer.parseInt(parts[3]);
                    for (int i = 0; i < count; i++) {
                        String info = parts[4 + i];
                        String[] file = info.split(":");
                        String name = file[0];
                        long size = Long.parseLong(file[1]);
                        Item item = new Item(name, size, peer);
                        if (catalog.containsKey(item)) {
                            catalog.get(item).addNeighbor(peer);
                        } else {
                            catalog.put(item, item);
                        }
                    }
                } catch (IOException e) {
                    peer.setOffline();
                    peer.printUpdate(Status.OFFLINE);
                }
            }
        }

        // 2) Exibe menu de arquivos encontrados
        System.out.println("Arquivos encontrados na rede:");
        System.out.println("Nome | Tamanho | Peer");
        System.out.println("[ 0] <Cancelar>");
        List<Item> options = new ArrayList<>(catalog.values());
        for (int i = 0; i < options.size(); i++) {
            Item e = options.get(i);
            System.out.printf("[%2d] %s | %d | ", i + 1, e.getName(), e.getSize());
            for (int j = 0; j < e.getPeers().size(); j++) {
                NeighborPeer p = e.getPeers().get(j);
                System.out.print(p.getAddress() + ":" + p.getPort()
                                 + (j < e.getPeers().size() - 1 ? "," : ""));
            }
            System.out.println();
        }

        // 3) Escolhe o arquivo para download
        System.out.print("Digite o número do arquivo para fazer o download: ");
        int choice = input.hasNextInt() ? input.nextInt() : 0;
        if (choice <= 0 || choice > options.size()) return;

        Item chosen = options.get(choice - 1);
        System.out.println("Arquivo escolhido: " + chosen.getName());

        int localChunk = FilePeer.getInstance().getChunk();
        int nChunks = (int)(chosen.getSize() / localChunk) 
                      + (chosen.getSize() % localChunk == 0 ? 0 : 1);

        String[] chunks = new String[nChunks];
        AtomicBoolean[] success = new AtomicBoolean[nChunks];
        List<Thread> threads = new LinkedList<>();

        // 4) Inicia medição de tempo
        long startTime = System.nanoTime();

        // 5) Cria e dispara threads de download de cada chunk
        for (int j = 0; j < nChunks; j++) {
            success[j] = new AtomicBoolean(true);
            DownloadChunk task = new DownloadChunk(chosen, j, localChunk, chunks, args, success[j]);
            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start();
        }

        // 6) Aguarda todas finalizarem
        for (Thread t : threads) {
            try { t.join(); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // 7) Verifica sucesso
        for (AtomicBoolean b : success) {
            if (!b.get()) {
                System.out.println("Não foi possível realizar o download");
                return;
            }
        }

        // 8) Monta arquivo a partir dos chunks e grava localmente
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            for (String chunk : chunks) {
                output.write(Base64.getDecoder().decode(chunk));
            }
            File outFile = new File(directory, chosen.getName());
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                fos.write(output.toByteArray());
            }
        } catch (IOException e) {
            System.out.println("Não foi possível salvar o arquivo " + chosen.getName());
            return;
        }

        // 9) Registra estatísticas de download
        double duration = (System.nanoTime() - startTime) / 1e9;
        int peerCount = chosen.getPeers().size();
        long fileSize = chosen.getSize();
        StatsManager.getInstance()
                    .addRecord(new StatisticRecord(localChunk, peerCount, fileSize, duration));

        // 10) Mensagem final
        System.out.println("Download do arquivo " + chosen.getName() + " finalizado.");
    }

    /** Representa uma entrada de arquivo na rede, com peers que o possuem */
    private static class Item {
        private final String name;
        private final long size;
        private final List<NeighborPeer> peers = new ArrayList<>();

        Item(String name, long size, NeighborPeer peer) {
            this.name = name;
            this.size = size;
            this.peers.add(peer);
        }

        void addNeighbor(NeighborPeer peer) {
            peers.add(peer);
        }

        String getName() { return name; }
        long getSize()   { return size;  }
        List<NeighborPeer> getPeers() { return peers; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Item)) return false;
            Item other = (Item) o;
            return size == other.size && name.equals(other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, size);
        }
    }

    /** Thread que baixa um único chunk de forma concorrente */
    private static class DownloadChunk implements Runnable {
        private final Item file;
        private final int index;
        private final int chunkSize;
        private final String[] chunks;
        private final String[] args;
        private final AtomicBoolean success;

        DownloadChunk(Item file, int index, int chunkSize,
                      String[] chunks, String[] args, AtomicBoolean success) {
            this.file = file;
            this.index = index;
            this.chunkSize = chunkSize;
            this.chunks = chunks;
            this.args = args;
            this.success = success;
        }

        @Override
        public void run() {
            int attempts = 0;
            int total = file.getPeers().size();
            while (attempts < total) {
                NeighborPeer peer = file.getPeers().get((index + attempts) % total);
                if (peer.getStatus() == Status.ONLINE) {
                    try {
                        args[1] = Integer.toString(FilePeer.getInstance().tick());
                        String[] dlArgs = {
                            peer.getAddress() + ":" + peer.getPort(),
                            args[1],
                            Integer.toString(chunkSize),
                            Integer.toString(index)
                        };
                        String resp = peer.connect(TypeMessages.DL, dlArgs);
                        String[] parts = resp.split(" ");
                        FilePeer.getInstance().tick(Integer.parseInt(parts[1]));
                        chunks[index] = parts.length >= 7 ? parts[6] : "";
                        return;
                    } catch (IOException e) {
                        peer.setOffline();
                        peer.printUpdate(Status.OFFLINE);
                    }
                }
                attempts++;
            }
            success.set(false);
        }
    }
}

