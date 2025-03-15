package com.usp.machines;

import com.usp.items.*;
import com.usp.machines.commands.*;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;

public class FileServer {
	private String address;
	private int port;
	private int clock;
	private List<NeighborPeer> peerList;
	private HashMap<Integer, Command> commands;
	
	public FileServer(String address, int port) {
		this.address = address;
		this.port = port;
		clock = 0;
		peerList = new ArrayList<>();
		commands = new HashMap<>();
		initCommands();
	}
	
	private void initCommands() {
		commands.put(1, new ListPeers(peerList));
		commands.put(2, new GetPeers(peerList));
		commands.put(3, new ListFiles());
		commands.put(4, new Exit(peerList));
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getClock() {
		return clock;
	}
	
	public void increaseClock() {
		clock++;
	}
	
	private void printCommands() {
		System.out.println("Escolha um comando:");
		System.out.println("     [1] Listar peers");
		System.out.println("     [2] Obter peers");
		System.out.println("     [3] Listar arquivos locais");
		System.out.println("     [4] Buscar arquivos");
		System.out.println("     [5] Exibir estatísticas");
		System.out.println("     [6] Alterar tamanho de chumk");
		System.out.println("     [9] Sair");
	}
	
	public void run() throws InvalidParametersException{
		if(address.isEmpty() || port == -1) throw new InvalidParametersException();
		// Processando lista de vizinhos...
		int codeIn = 0; 
		while(codeIn != 9) {
			printCommands();
			System.out.print(">");
			Scanner input = new Scanner(System.in);
			codeIn = input.nextInt();
			commands.get(codeIn).execute();
			input.close();
		}
	}
}
