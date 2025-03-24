package com.usp.machines.actions;

import java.util.List;

import com.usp.items.NeighborFilePeer;
import com.usp.items.NeighborPeer;
import com.usp.items.Status;

public class ByeAction implements Action {
	private List<NeighborPeer> list;
	
	public ByeAction(List<NeighborPeer> list) {
		this.list = list;
	}
	
	public String response(String[] args) {
		String[] result = args[0].split(":");//<endereço>:<porta>
		String address = result[0]; //IP
		int port = Integer.parseInt(result[1]); //porta
		boolean notExists = true;
		for(int i = 0; i < list.size(); i++) {
			NeighborPeer peer = list.get(i);
			if(peer.getAddress().equals(address) && peer.getPort() == port) {
				peer.setOffline(); //atualiza status
				peer.printUpdate(Status.OFFLINE);
				notExists = false; //peer existe na lista
				break;
			}
		}
		if(notExists) { //Adicionando peer novo a lista
			NeighborPeer peer = new NeighborFilePeer(address, port); //cria peer novo
			peer.setOffline();
			peer.printAddPeer(Status.OFFLINE);
			list.add(peer); //adiciona na lista
		}
		return null; //não há resposta
		
	}
}
