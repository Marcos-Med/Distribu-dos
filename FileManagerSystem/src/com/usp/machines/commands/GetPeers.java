package com.usp.machines.commands;

import com.usp.items.FilePeer;
import com.usp.items.NeighborPeer;
import com.usp.items.Status;
import com.usp.items.TypeMessages;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import com.usp.machines.actions.PeerListAction;
import com.usp.machines.actions.Action;

public class GetPeers implements Command { //Comando GET_PEERS
	private List<NeighborPeer> list;
	private Action actionPeerList;
	
	public GetPeers(List<NeighborPeer> list) {
		this.list = list;
		actionPeerList = new PeerListAction(list);
	}
	
	public void execute(String[] args) {
		List<NeighborPeer> listCurrent;
		synchronized(list) {
			listCurrent = new LinkedList<>(list);
		}
		for(int i = 0; i < listCurrent.size(); i++) {
			NeighborPeer peer = listCurrent.get(i);
			try {
				args[1] = Integer.toString(FilePeer.getInstance().tick());
				String response = peer.connect(TypeMessages.GET_PEERS, args); //envia GET_PEERS para cada peer
				String[] result = response.split(" ");
				FilePeer.getInstance().tick(Integer.parseInt(result[1])); //respondeu, incremento clock
				peer.setOnline();
				peer.printUpdate(Status.ONLINE);
				peer.setClock(Integer.parseInt(result[1]));
				actionPeerList.response(result);// processa a mensagem PEER_LIST
			}
			catch(IOException e){//Caso peer esteja offline
				peer.setOffline();
				peer.printUpdate(Status.OFFLINE);
			}
		}
	}
}
