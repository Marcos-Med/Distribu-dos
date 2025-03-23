package com.usp.machines.commands;

import com.usp.items.FilePeer;
import com.usp.items.NeighborPeer;
import com.usp.items.Status;
import com.usp.items.TypeMessages;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ListPeers implements Command { //Comando para lista os peers conhecidos
	private List<NeighborPeer> list;
	public ListPeers(List<NeighborPeer> list) {
		this.list = list;
	}
	private void print() {
		System.out.println("Lista de peers:");
		System.out.println("     [0] voltar ao menu anterior");
	}
	public void execute(String[] args) {
		int option;
		do {
			print();
			int i = 1;
			for(NeighborPeer peer: list) { //imprime menu de seleção
				String output = "[" + i + "]";
				output = output + " " + peer.getAddress() + ":" + peer.getPort();
				output += (" " + peer.getStatus());
				System.out.println(output);
				i++;
			}
			Scanner input = new Scanner(System.in);
			System.out.print(">");
			option = input.nextInt();
			if(option != 0) { //Se for selecionado um peer, mande HELLO
				NeighborPeer peer = list.get(option - 1);
				try {
					args[1] = Integer.toString(FilePeer.getInstance().tick());
					peer.connect(TypeMessages.HELLO, args);//HELLO
					peer.setOnline();
					peer.printUpdate(Status.ONLINE);
				}
				catch(IOException e) { //Peer não respondeu
					peer.setOffline();
					peer.printUpdate(Status.OFFLINE);
					e.printStackTrace();
				}
			}
			input.close();
		} while(option != 0); //Enquanto não for digitado "voltar"
	}
}
