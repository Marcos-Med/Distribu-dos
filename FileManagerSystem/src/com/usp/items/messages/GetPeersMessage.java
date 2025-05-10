package com.usp.items.messages;

import com.usp.items.TypeMessages;

public class GetPeersMessage extends Message { // Mensagem GetPeers
	public GetPeersMessage() {
		super();
	}
	public String buildMessage(String[] args) {
		String origin = args[0]; //origem
		String clock = args[1]; //clock
		TypeMessages type = TypeMessages.GET_PEERS;
		return this.buildHead(origin, Integer.parseInt(clock), type) + "\n"; //constrói mensagem
	}
}
