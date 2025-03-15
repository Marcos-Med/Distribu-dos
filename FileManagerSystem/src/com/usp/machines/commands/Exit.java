package com.usp.machines.commands;

import com.usp.items.NeighborPeer;
import com.usp.items.Status;

import java.io.IOException;
import java.util.List;

public class Exit implements Command {
	private List<NeighborPeer> list;
	
	public Exit(List<NeighborPeer> list) {
		this.list = list;
	}
	
	public void execute() {
		System.out.println("Saindo...");
		for(NeighborPeer peer:list) {
			try {
				if(peer.getStatus() == Status.ONLINE) peer.connect(null);//BYE
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
