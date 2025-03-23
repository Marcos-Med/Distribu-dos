package com.usp.items.messages;

import com.usp.items.TypeMessages;

public class ByeMessage extends Message { //Mensagem Bye
	public ByeMessage() {
		super();
	}
	
	public String buildMessage(String[] args) {
		String origin = args[0]; //origem
		String clock = args[1]; //clock
		TypeMessages type = TypeMessages.BYE;
		return this.buildHead(origin, Integer.parseInt(clock), type) + "\n"; //constrói mensagem
	}
}
