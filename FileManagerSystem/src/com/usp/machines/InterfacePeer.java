package com.usp.machines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.usp.items.NeighborFilePeer;
import com.usp.items.NeighborPeer;
import com.usp.machines.commands.Command;
import com.usp.machines.commands.Exit;
import com.usp.machines.commands.GetPeers;
import com.usp.machines.commands.ListFiles;
import com.usp.machines.commands.ListPeers;

public class InterfacePeer implements Runnable {
	private String address;
	private int port;
	private String fileDirectory;
	private List<NeighborPeer> peerList;
	private HashMap<Integer, Command> commands;
	private FileServer server;
	
	public InterfacePeer(String address, int port, String FilePeers, String directory, FileServer server) {
		this.address = address;
		this.port = port;
		fileDirectory = directory;
		createListPeer(FilePeers);
		commands = new HashMap<>();
		initCommands();
		this.server = server;
	}
	
	private void createListPeer(String FilePeers) {
		peerList = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader(FilePeers))){
			String line;
			while((line = br.readLine()) != null) {
				String[] result = line.split(":");
				peerList.add(new NeighborFilePeer(result[0], Integer.parseInt(result[1])));
			}
		}
		catch(IOException e) {
			System.out.println("Erro ao ler o arquivo: " + e.getMessage());
			System.exit(1); //Erro de arquivo
		}
		
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
	
	public void run() {
		int codeIn;
		do {
			printCommands();
			System.out.print(">");
			Scanner input = new Scanner(System.in);
			codeIn = input.nextInt();
			commands.get(codeIn).execute();
			input.close();
		} while(codeIn != 9);
		server.off();
		
	}
}
