package com.usp.items;

import java.io.*;
import java.net.*;

public class NeighborFilePeer implements NeighborPeer{ //Peer Conhecido
	private String address;
	private int port;
	private Status status;
	private BuilderMessage builderMessage;
	
	public NeighborFilePeer(String address, int port) {
		this.address = address;
		this.port = port;
		status = Status.OFFLINE;
		builderMessage = BuilderMessage.getInstance();
	}
	
	public String getAddress() { //retorna endereço IP
		return address;
	}
	
	public int getPort() { //retorna número de Porta
		return port;
	}
	
	public Status getStatus() { //retorna Status
		return status;
	}
	
	public void setOnline() { //liga peer
		status = Status.ONLINE;
	}
	
	public void setOffline() { //desliga peer
		status = Status.OFFLINE;
	}
	
	public void  printUpdate(Status status) { //Imprime no terminal a atualização do peer
		System.out.println("Atualizando peer " + getAddress() + ":" + getPort() + " status " + status);
	}
	
	public void printAddPeer(Status status) { //Imprime quando adiciona um novo peer na lista
		System.out.println("Adicionando novo peer " + getAddress() + ":" + getPort() + " status " + status);
	}
	
	public String connect(TypeMessages request, String[] args) throws IOException{ //envia comandos ao peer
		try(Socket socket = new Socket(address, port)){
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			output.print(builderMessage.buildMessage(request, args)); //envia a requisição
			output.flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return input.readLine(); //retorna a resposta
		}
	}
}
