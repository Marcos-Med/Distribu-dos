package com.usp.machines.commands;
import com.usp.items.FilePeer;
import com.usp.items.TypeMessages;
import com.usp.items.Status;
import com.usp.items.NeighborPeer;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import com.usp.machines.InterfacePeer;

public class SearchFile implements Command {
    private List<NeighborPeer> peerList;
    private String directory;

    public SearchFile(List<NeighborPeer> peerList, String directory) {
        this.peerList = peerList;
        this.directory = directory;
    }

    public void execute(String[] args) {
        Scanner input = InterfacePeer.getInput();
        class Entry { String name; long size; String peerAddr; NeighborPeer pev; }
        List<Entry> catalog = new ArrayList<>(); //catalogos de arquivos e respectivos peers

        // varre peers online e solicita LS
        for (NeighborPeer peer : peerList) {
            if (peer.getStatus() == Status.ONLINE) {
                try {
                    args[1] = Integer.toString(FilePeer.getInstance().tick());
                    String resp = peer.connect(TypeMessages.LS, args); //solicita os arquivos disponíveis
                    String[] parts = resp.split(" ");
                    FilePeer.getInstance().tick(Integer.parseInt(parts[1]));
                    peer.setOnline(); 
                    peer.printUpdate(Status.ONLINE);
                    peer.setClock(Integer.parseInt(parts[1]));
                    int count = Integer.parseInt(parts[3]);
                    for (int i = 0; i < count; i++) {
                        String info = parts[4 + i];
                        String[] file = info.split(":"); 
                        Entry e = new Entry();
                        e.name = file[0];
                        e.size = Long.parseLong(file[1]);
                        e.peerAddr = peer.getAddress() + ":" + peer.getPort();
                        e.pev = peer;
                        catalog.add(e);
                    }
                } catch (IOException e) {
                    peer.setOffline(); 
                    peer.printUpdate(Status.OFFLINE);
                }
            }
        }

        System.out.println("Arquivos encontrados na rede:");
        System.out.println("Nome | Tamanho | Peer");
        System.out.println("[ 0] <Cancelar> | |");
        for (int i = 0; i < catalog.size(); i++) {
            Entry e = catalog.get(i);
            System.out.printf("[ %d] %s | %d | %s\n", i+1, e.name, e.size, e.peerAddr); //arquivos disponíveis
        }
        System.out.print("Digite o numero do arquivo para fazer o download:\n>");
        int opt = input.hasNextInt() ? input.nextInt() : 0;
        if (opt > 0 && opt <= catalog.size()) {
            Entry choose = catalog.get(opt - 1); //arquivo escolhido
            System.out.println("arquivo escolhido " + choose.name);
            try {
                args[1] = Integer.toString(FilePeer.getInstance().tick());
                String[] dlArgs = new String[]{ args[0], args[1], choose.name, "0", "0" };
                String fresp = choose.pev.connect(TypeMessages.DL, dlArgs);
                String[] fp = fresp.split(" ");
                FilePeer.getInstance().tick(Integer.parseInt(fp[1]));
                choose.pev.setClock(Integer.parseInt(fp[1]));
                choose.pev.setOnline(); choose.pev.printUpdate(Status.ONLINE);
                String content;
                if(fp.length != 7) content = ""; //caso o arquivo esteja vazio
                else content = fp[6];
                byte[] data = Base64.getDecoder().decode(content);
                try (FileOutputStream fos = new FileOutputStream(directory + "/" + choose.name)) {
                    fos.write(data); //salva arquivo
                }
                System.out.println("Download do arquivo " + choose.name + " finalizado.");
            } catch (IOException e) {
            	choose.pev.setOffline();
				choose.pev.printUpdate(Status.OFFLINE);
            }
        }
    }
}