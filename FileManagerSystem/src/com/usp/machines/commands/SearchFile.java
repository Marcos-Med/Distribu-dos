package com.usp.machines.commands;
import com.usp.items.FilePeer;
import com.usp.items.TypeMessages;
import com.usp.items.Status;
import com.usp.items.NeighborPeer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.IOException;
import com.usp.machines.InterfacePeer;

public class SearchFile implements Command { //Comando resposável por listar e buscar arquivos
    private List<NeighborPeer> peerList;
    private String directory;

    public SearchFile(List<NeighborPeer> peerList, String directory) {
        this.peerList = peerList;
        this.directory = directory;
    }

    public void execute(String[] args) {
        Scanner input = InterfacePeer.getInput();
        HashMap<Item, Item> catalog = new HashMap<>(); //catalogo de arquivos e respectivos peers

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
                    for (int i = 0; i < count; i++) { //gera uma lista de arquivos
                        String info = parts[4 + i];
                        String[] file = info.split(":"); 
                        String name = file[0];
                        long size = Long.parseLong(file[1]);
                        Item e = new Item(name, size, peer);
                        if(catalog.containsKey(e)) {
                        	catalog.get(e).addNeighbor(peer);//se já existe, apenas adiciona o peer que também contém o arquivo
                        }
                        else catalog.put(e, e);
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
        Collection<Item> values = catalog.values();
        int i = 0;
        for (Item e: values) {
        	i++;
            System.out.printf("[ %d] %s | %d | ", i, e.getName(), e.getSize()); //arquivos disponíveis
            List<NeighborPeer> neighbors = e.getPeers();
            for(int j = 0; j < neighbors.size(); j++) { //agrupa todos os peers que possuem o msm arquivo
            	NeighborPeer p = neighbors.get(j);
            	String address = p.getAddress()+":"+p.getPort();
            	if(j != neighbors.size() - 1) address += ",";
            	System.out.printf("%s ", address);
            }
            System.out.printf("\n");
        }
        List<Item> options = new ArrayList<>(catalog.values());
        System.out.print("Digite o numero do arquivo para fazer o download:\n>");
        int opt = input.hasNextInt() ? input.nextInt() : 0;
        if (opt > 0 && opt <= options.size()) {
            Item choose = options.get(opt - 1); //arquivo escolhido
            System.out.println("arquivo escolhido " + choose.getName());
            int local_chunk = FilePeer.getInstance().getChunk(); //chunk local
            int n_chunks = (int) choose.getSize() / local_chunk; // número de chunks
            if(choose.getSize() % local_chunk != 0) n_chunks++;
            String[] chunks = new String[n_chunks];
            AtomicBoolean[] sucess = new AtomicBoolean[n_chunks];
            List<Thread> threads = new LinkedList<>();
            for(int j = 0; j < n_chunks; j++) {
            	sucess[j] = new AtomicBoolean(true);//assume inicialmente que houve sucesso
            	DownloadChunk task = new DownloadChunk(choose, j, local_chunk, chunks, args, sucess[j]);
            	Thread thread = new Thread(task);
            	threads.add(thread);
            	thread.start();
            }
            for(Thread thread: threads) { //continua apenas se todas as threads foram executadas
            	try {
            		thread.join();
            	}
            	catch(InterruptedException exceptionThread) {
            		System.out.println("Thread interrompida");
            	}
            }
            boolean allOK = true;
            for(AtomicBoolean s: sucess) {
            	if(!s.get()) { //se alguma thread falhou
            		allOK = false;
            		break;
            	}
            }
            if(!allOK) System.out.println("Não foi possível realizar o download");
            else {
            	ByteArrayOutputStream output = new ByteArrayOutputStream();
            	for(String chunk: chunks) {
            		byte[] data = Base64.getDecoder().decode(chunk);
            		output.writeBytes(data);
            	}
            	File file = new File(directory, choose.getName()); //abre diretório
            	try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(output.toByteArray()); //salva arquivo
                    System.out.println("Download do arquivo " + choose.getName() + " finalizado.");
                }
            	catch(IOException e) {
            		System.out.println("Não foi possível salvar o arquivo " + choose.getName());
            	}
            }
        }
    }
}

class Item {
	private String name;
	private long size;
	private List<NeighborPeer> list_peers;
	
	public Item(String name, long size, NeighborPeer peer) {
		this.name = name;
		this.size = size;
		list_peers = new LinkedList<>();
		list_peers.add(peer);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Item)) return false;
		Item other = (Item) obj; //casting
		return size == other.getSize() && name.equals(other.getName()); // Dois arquivos são iguais com base no size e no nome
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, size);
	}
	
	public String getName() {
		return this.name;
	}
	
	public long getSize() {
		return this.size;
	}
	
	public void addNeighbor(NeighborPeer peer) {
		list_peers.add(peer);
	}
	
	public List<NeighborPeer> getPeers(){
		return list_peers;
	}
}

class DownloadChunk implements Runnable{
	private Item fileChoose;
	private int index_chunk;
	private int local_chunk;
	private String[] chunks;
	private String[] args;
	private AtomicBoolean sucess;
	
	public DownloadChunk(Item fileChoose, int index_chunk, int local_chunk, String[] chunks, String[] args, AtomicBoolean sucess) {
		this.fileChoose = fileChoose;
		this.index_chunk = index_chunk;
		this.local_chunk = local_chunk;
		this.args = args;
		this.chunks = chunks;
		this.sucess = sucess;
		
	}
	
	public void run() {
		int error;
		for(error = 0; error < fileChoose.getPeers().size(); error++) {
			int index_peer = (index_chunk % fileChoose.getPeers().size() + error) % fileChoose.getPeers().size(); //peer onde será feito o download
			try {
        		args[1] = Integer.toString(FilePeer.getInstance().tick());
        		String[] dlArgs = new String[]{ args[0], args[1], fileChoose.getName(), Integer.toString(local_chunk), 
        				Integer.toString(index_chunk)};
        		String fresp = fileChoose.getPeers().get(index_peer).connect(TypeMessages.DL, dlArgs);
                String[] fp = fresp.split(" ");
                FilePeer.getInstance().tick(Integer.parseInt(fp[1]));
                fileChoose.getPeers().get(index_peer).setClock(Integer.parseInt(fp[1]));
                fileChoose.getPeers().get(index_peer).setOnline(); 
                fileChoose.getPeers().get(index_peer).printUpdate(Status.ONLINE);
                String content;
                if(fp.length != 7) content = ""; //caso o arquivo esteja vazio
                else content = fp[6];
                chunks[index_chunk] = content;
                break;
        	}
        	catch(IOException e) {
        		fileChoose.getPeers().get(index_peer).setOffline();
        		fileChoose.getPeers().get(index_peer).printUpdate(Status.OFFLINE);
        	}
		}
		if(error == fileChoose.getPeers().size()) sucess.set(false);
	}
}
