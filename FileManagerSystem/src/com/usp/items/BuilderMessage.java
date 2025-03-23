package com.usp.items;

import java.util.HashMap;

import com.usp.items.messages.*;

public class BuilderMessage { //Construtor de mensagens suportadas pelo sistema
	private static BuilderMessage instance;
	private HashMap<TypeMessages, Message> actions;
	
	private BuilderMessage() {
		createActions();
	}
	
	public static BuilderMessage getInstance() { //Design Patterns Singleton
		if(instance == null) instance = new BuilderMessage();
		return instance;
	}
	
	private void createActions(){ // cria o HashMap contendo as ações para criar as mensagens
		actions = new HashMap<>();
		actions.put(TypeMessages.HELLO, new HelloMessage());
		actions.put(TypeMessages.BYE, new ByeMessage());
		actions.put(TypeMessages.GET_PEERS, new GetPeersMessage());
		actions.put(TypeMessages.PEER_LIST, new PeerListMessage());
	}
	
	public String buildMessage(TypeMessages type, String[] args) { //Constrói a mensagem
		return actions.get(type).buildMessage(args);
	}
}
