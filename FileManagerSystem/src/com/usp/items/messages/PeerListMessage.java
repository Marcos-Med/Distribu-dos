package com.usp.items.messages;

import com.usp.items.TypeMessages;

public class PeerListMessage extends Message { //Mensagem PeerList
	public PeerListMessage() {
		super();
	}
	
	public String buildMessage(String[] args) {
		String origin = args[0]; //origem
		String clock = args[1]; //clock
		int quantity = Integer.parseInt(args[2]); //quantidade de peers listados
		
		TypeMessages type = TypeMessages.PEER_LIST;
		String result = this.buildHead(origin, Integer.parseInt(clock), type) + getSpace() +
			quantity;
		for(int i = 3; i < quantity + 3; i++) {
			result += getSpace(); //espaço em branco
			result += args[i]; //peer
		}
		return result += "\n";
	}
}
