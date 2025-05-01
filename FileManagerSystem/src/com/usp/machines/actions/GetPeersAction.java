package com.usp.machines.actions;

import java.util.List;

import com.usp.items.BuilderMessage;
import com.usp.items.FilePeer;
import com.usp.items.NeighborFilePeer;
import com.usp.items.NeighborPeer;
import com.usp.items.Status;
import com.usp.items.TypeMessages;

public class GetPeersAction implements Action {
	private List<NeighborPeer> list;
	private BuilderMessage builderMessage; //construtor de mensagem
	
	public GetPeersAction(List<NeighborPeer> list) {
		this.list = list;
		builderMessage = BuilderMessage.getInstance();
	}
	
	public String response(String[] args) {
		String[] result = args[0].split(":");
		String address = result[0];
		int port = Integer.parseInt(result[1]);
		int clock = Integer.parseInt(args[1]);//clock
		boolean notExists = true;
		for(int i = 0; i < list.size(); i++) {
			NeighborPeer peer = list.get(i);
			if(peer.getAddress().equals(address) && peer.getPort() == port)  {
				peer.setOnline(); //atualiza status
				peer.printUpdate(Status.ONLINE);
				peer.setClock(clock);
				notExists = false;
				break;
			}
		}
		if(notExists) {
			NeighborPeer peer = new NeighborFilePeer(address, port); //cria peer novo
			peer.setOnline();
			peer.printAddPeer(Status.ONLINE);
			peer.setClock(clock);//altera clock
			list.add(peer); //adiciona na lista
		}
		int len = list.size() - 1; //não insere o remetente
		String[] rs = new String[len + 3];
		FilePeer filePeer = FilePeer.getInstance();
		rs[0] = filePeer.getAddress()+":"+filePeer.getPort(); //origem
		rs[2] = Integer.toString(len); //quantidade
		int i = 3;
		for(int j = 0; j < list.size(); j++) {
			NeighborPeer peer = list.get(j);
			if(!peer.getAddress().equals(address) || !(peer.getPort() == port)) {
				String idPeer = peer.getAddress() + ":" +
						peer.getPort() + ":" + peer.getStatus() + ":" + peer.getClock(); //identificação do peer na lista
				rs[i] = idPeer;
				i++;
			}
		}
		rs[1] = Integer.toString(filePeer.tick());//clock
		return builderMessage.buildMessage(TypeMessages.PEER_LIST, rs); //envia a resposta
	}
}
