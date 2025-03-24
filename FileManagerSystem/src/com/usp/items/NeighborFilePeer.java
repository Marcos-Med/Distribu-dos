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
		String message = builderMessage.buildMessage(request, args);
		System.out.println("Encaminhando mensagem \"" + message.replace("\n", "") + "\" para " +address+":"+port);
		try(Socket socket = new Socket(address, port)){
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			output.print(message); //envia a requisição
			output.flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			message = input.readLine(); //resposta do peer
			if(!message.equals("")) System.out.println("Resposta recebida:\"" + message + "\""); //se houve resposta
			return message; //retorna a resposta
		}
	}
}
