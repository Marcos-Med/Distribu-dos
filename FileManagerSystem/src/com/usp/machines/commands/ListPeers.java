package com.usp.machines.commands;

import com.usp.items.FilePeer;
import com.usp.items.NeighborPeer;
import com.usp.items.Status;
import com.usp.items.TypeMessages;
import com.usp.machines.InterfacePeer;

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
		int option = -1;
		Scanner input = InterfacePeer.getInput();
		do {
			print();
			int i = 1;
			for(int j = 0; j < list.size(); j++) { //imprime menu de seleção
				NeighborPeer peer = list.get(j);
				String output = "     ";
				output += "[" + i + "]";
				output = output + " " + peer.getAddress() + ":" + peer.getPort();
				output += (" " + peer.getStatus());
				output += (" (clock: " + peer.getClock() + ")");
				System.out.println(output);
				i++;
			}
			System.out.print(">");
			if(input.hasNextInt()) {
				option = input.nextInt();
				if(option > 0 && option <= list.size()) { //Se for selecionado um peer, mande HELLO
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
					}
				}
			}
		} while(option != 0); //Enquanto não for digitado "voltar"
	}
}
