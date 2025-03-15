package com.usp.machines.commands;

import com.usp.items.NeighborPeer;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ListPeers implements Command {
	private List<NeighborPeer> list;
	public ListPeers(List<NeighborPeer> list) {
		this.list = list;
	}
	private void print() {
		System.out.println("Lista de peers:");
		System.out.println("     [0] voltar ao menu anterior");
	}
	public void execute() {
		int option = -1;
		while(option != 0) {
			print();
			int i = 1;
			for(NeighborPeer peer: list) {
				String output = "[" + i + "]";
				output = output + " " + peer.getAddress() + ":" + peer.getPort();
				output += (" " + peer.getStatus());
				System.out.println(output);
				i++;
			}
			Scanner input = new Scanner(System.in);
			System.out.print(">");
			option = input.nextInt();
			if(option != 0) {
				try {
					NeighborPeer peer = list.get(option - 1);
					peer.connect(null);//HELLO
					peer.setOnline();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			input.close();
		}
	}
}
