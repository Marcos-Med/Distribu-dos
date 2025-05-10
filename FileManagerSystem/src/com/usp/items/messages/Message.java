package com.usp.items.messages;

import com.usp.items.TypeMessages;

public abstract class Message {
	
	private String space;
	
	protected Message(){
		space = " ";
	}
	
	public abstract String buildMessage(String[] args); //constrói a mensagem desejada
	
	protected String getSpace() { // retorna espaço em branco
		return space;
	}
	
	protected String buildHead(String origin, int clock, TypeMessages type) { //constrói o cabeçalho da mensagem
		return origin + space + clock + space + type;
	}
}
