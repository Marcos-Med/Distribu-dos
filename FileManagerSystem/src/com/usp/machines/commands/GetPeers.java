package com.usp.machines.commands;

import com.usp.items.NeighborPeer;

import java.io.IOException;
import java.util.List;

public class GetPeers implements Command {
	private List<NeighborPeer> list;
	
	public GetPeers(List<NeighborPeer> list) {
		this.list = list;
	}
	
	public void execute() {
		for(NeighborPeer peer: list) {
			try {
				String response = peer.connect(null);
				System.out.println(response);
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
