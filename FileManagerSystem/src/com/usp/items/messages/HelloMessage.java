package com.usp.items.messages;

import com.usp.items.TypeMessages;

public class HelloMessage extends Message{ // Mensagem Hello
	public HelloMessage() {
		super();
	}
	public String buildMessage(String[] args) {
		String origin = args[0]; //origem
		String clock = args[1]; //clock
		TypeMessages type = TypeMessages.HELLO;
		return this.buildHead(origin, Integer.parseInt(clock), type) + "\n"; //constrói mensagem
	}
}
