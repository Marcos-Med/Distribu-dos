package com.usp.items;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.usp.machines.actions.*;
import com.usp.machines.commands.Command;
import com.usp.machines.commands.*;

public class FilePeer {
	private static FilePeer object; //Design Patterns Singleton
	private String address; //IP
	private int port; //Porta
	private int clock;
	private String fileDirectory; //Diretório de arquivos
	private List<NeighborPeer> peerList; //Lista de Peers conhecidos
	private HashMap<TypeMessages, Action> responses; //respostas disponíveis no sistema (SERVER)
	private HashMap<Integer, Command> commands; //comandos disponíveis no sistema (CLIENT)
	
	
	private FilePeer(String address, int port, String FilePeers, String directory) {
		this.address = address;
		this.port = port;
		fileDirectory = directory;
		createListPeer(FilePeers);
		initResponses();
		initCommands();
		clock = 0;
		
	}
	
	public static FilePeer getInstance(String address, int port, String FilePeers, String directory) {
		if(object == null) object = new FilePeer(address, port, FilePeers, directory); //Use no método na primeira vez
		return object;
	}
	
	public static FilePeer getInstance() { //Use nas demais vezes
		if(object == null) throw new IllegalStateException("A instância ainda não foi inicializada. Chame getInstance(params...) primeiro.");
		return object;
	}
	
	private void createListPeer(String FilePeers) { //inicia a lista de peers conhecidos
		peerList = Collections.synchronizedList(new ArrayList<>());
		try(BufferedReader br = new BufferedReader(new FileReader(FilePeers))){
			String line;
			while((line = br.readLine()) != null) { //enquanto há peers na lista
				String[] result = line.split(":");
				peerList.add(new NeighborFilePeer(result[0], Integer.parseInt(result[1]))); //adiciona peer na lista
			}
		}
		catch(IOException e) {
			System.out.println("Erro ao ler o arquivo: " + e.getMessage());
			System.exit(1); //Erro de arquivo
		}
		
	}
	
	private void printClock() {
		System.out.println("Atualizando relógio para " + clock); //Imprime o valor do relógio
	}
	
	public synchronized int tick() { //Alteração do clock ao enviar mensagens
	      clock++; //Evita condição de corrida
	      printClock();
	      return clock;
	}
	
	public synchronized int tick(int clock) { //Alteração do clock ao receber mensagens
		if (this.clock < clock) this.clock = clock; //clock_local = max(clock_local, clock_msg)
		this.clock++;
		printClock();
		return this.clock;
	}
	
	private void initResponses() { //respostas disponíveis
		responses = new HashMap<>();
		responses.put(TypeMessages.HELLO, new HelloAction(peerList));
		responses.put(TypeMessages.BYE, new ByeAction(peerList));
		responses.put(TypeMessages.GET_PEERS, new GetPeersAction(peerList));
		responses.put(TypeMessages.PEER_LIST, new PeerListAction(peerList));
		responses.put(TypeMessages.LS, new LSAction(fileDirectory, peerList));
	    responses.put(TypeMessages.DL, new DLAction(fileDirectory, peerList));
	}
	
	private void initCommands() { //comandos disponíveis
		commands = new HashMap<>();
		commands.put(1, new ListPeers(peerList));
		commands.put(2, new GetPeers(peerList));
		commands.put(3, new ListFiles(fileDirectory));
		commands.put(9, new Exit(peerList));
		commands.put(4, new SearchFile(peerList, fileDirectory));
	}
	
	public String getAddress() { //retorna endereço IP
		return address;
	}
	
	public int getPort() { //retorna número de Porta
		return port;
	}
	
	public String response(TypeMessages type, String[] args) { //Responde uma request de algum peer
		return responses.get(type).response(args);
	}
	
	public void request(int command) { //emite comandos no próprio peer
		String[] args = new String[2];
		args[0] = address+":"+port;
		if(commands.containsKey(command)) commands.get(command).execute(args);
		else System.out.println("Comando inválido!");
	}
	

}
