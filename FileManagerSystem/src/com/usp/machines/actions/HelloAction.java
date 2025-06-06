package com.usp.machines.actions;

import com.usp.items.NeighborFilePeer;
import com.usp.items.NeighborPeer;
import com.usp.items.Status;

import java.util.List;

public class HelloAction implements Action{
	private List<NeighborPeer> list;
	public HelloAction(List<NeighborPeer> list) {
		this.list = list;
	}
	public String response(String[] args) {
		String[] result = args[0].split(":"); //<endereço>:<porta>
		String address = result[0]; //IP
		int port = Integer.parseInt(result[1]); //porta
		int clock = Integer.parseInt(args[1]);//clock
		boolean notExists = true;
		for(int i = 0; i < list.size(); i++) {
			NeighborPeer peer = list.get(i);
			if(peer.getAddress().equals(address) && peer.getPort() == port) {
				peer.setOnline(); //atualiza status
				peer.printUpdate(Status.ONLINE);
				peer.setClock(clock);//altera clock
				notExists = false; //peer existe na lista
				break;
			}
		}
		if(notExists) { //Adicionando peer novo a lista
			NeighborPeer peer = new NeighborFilePeer(address, port); //cria peer novo
			peer.setOnline();
			peer.printAddPeer(Status.ONLINE);
			peer.setClock(clock);//altera clock
			list.add(peer); //adiciona na lista
		}
		return null; //não há resposta
	}
}
