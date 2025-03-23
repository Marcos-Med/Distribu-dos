package com.usp.machines;

import java.util.Scanner;
import com.usp.items.FilePeer;

public class InterfacePeer {
	private FilePeer peer;
	
	public InterfacePeer(String address, int port, String FilePeers, String directory) {
		peer = FilePeer.getInstance(address, port, FilePeers, directory);
	}
	
	public String getAddress() { //retorna endereço IP
		return peer.getAddress();
	}
	
	public int getPort() { //retorna número de Porta
		return peer.getPort();
	}
	
	private void printCommands() { //exibe menu de seleção
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
		ServerPeer serverPeer = new ServerPeer(peer);
		Thread server = new Thread(serverPeer);//thread do servidor
		server.start();
		int codeIn;
		do {
			printCommands();
			System.out.print(">");
			Scanner input = new Scanner(System.in);
			codeIn = input.nextInt(); //seleciona comando
			peer.request(codeIn); //executa comando
			input.close(); //fecha leitor de entrada
		} while(codeIn != 9); //enquanto não digita "Sair"
	    serverPeer.off(); //encerra o servidor local
	}
}
