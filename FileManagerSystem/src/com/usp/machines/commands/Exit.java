package com.usp.machines.commands;

import com.usp.items.FilePeer;
import com.usp.items.NeighborPeer;
import com.usp.items.Status;
import com.usp.items.TypeMessages;

import java.io.IOException;
import java.util.List;

public class Exit implements Command { //Comando "Sair"
	private List<NeighborPeer> list; //lista de peers conhecidos
	
	public Exit(List<NeighborPeer> list) {
		this.list = list;
	}
	
	public void execute(String[] args) {
		System.out.println("Saindo...");
		for(NeighborPeer peer:list) { //Se peer estiver online, mande a mensagem BYE
			try {
				if(peer.getStatus() == Status.ONLINE) {
					args[1] = Integer.toString(FilePeer.getInstance().tick());
					peer.connect(TypeMessages.BYE, args);//BYE
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
