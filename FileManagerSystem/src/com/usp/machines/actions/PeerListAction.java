package com.usp.machines.actions;

import java.util.List;
import com.usp.items.Status;
import com.usp.items.NeighborPeer;
import com.usp.items.NeighborFilePeer;

public class PeerListAction implements Action{
	private List<NeighborPeer> list;
	
	public PeerListAction(List<NeighborPeer> list) {
		this.list = list;
	}
	
	public String response(String[] args) {
		int len = Integer.parseInt(args[3]);//tamanho da lista devolvida
		for(int i = 4; i < len + 4; i++) {
			String[] rs = args[i].substring(0, args[i].length() - 1).split(":");
			String address = rs[0]; //IP
			int port = Integer.parseInt(rs[1]); //Porta
			String status = rs[2]; //Status
			boolean exists = false;
			for(int j = 0; j < list.size(); j++) {
				NeighborPeer peer = list.get(j);
				if(peer.getAddress().equals(address) && peer.getPort() == port) { //Se o peer já é conhecido
					//Atualize o peer
					if(status.equals("ONLINE")) peer.setOnline();
					else peer.setOffline();
					peer.printUpdate(Status.valueOf(status));
					exists = true;
					break;
				}
			}
			if(!exists) {//Se não conhecia o peer
				//Insere o peer na lista
				NeighborPeer peer = new NeighborFilePeer(address, port);
				list.add(peer);
				if(status.equals("ONLINE")) peer.setOnline();
				else peer.setOffline();
				peer.printAddPeer(Status.valueOf(status));
			}
		}
		return null; //Não há retorno
	}
}
