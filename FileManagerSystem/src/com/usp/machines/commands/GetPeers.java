package com.usp.machines.commands;

import com.usp.items.FilePeer;
import com.usp.items.NeighborPeer;
import com.usp.items.Status;
import com.usp.items.TypeMessages;
import java.io.IOException;
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
		for(NeighborPeer peer: list) {
			try {
				args[1] = Integer.toString(FilePeer.getInstance().tick());
				String response = peer.connect(TypeMessages.GET_PEERS, args); //envia GET_PEERS para cada peer
				FilePeer.getInstance().tick(); //respondeu, incremento clock
				peer.setOnline();
				peer.printUpdate(Status.ONLINE);
				String[] result = response.split(" ");
				actionPeerList.response(result);// processa a mensagem PEER_LIST
			}
			catch(IOException e){//Caso peer esteja offline
				peer.setOffline();
				peer.printUpdate(Status.OFFLINE);
				e.printStackTrace();
			}
		}
	}
}
