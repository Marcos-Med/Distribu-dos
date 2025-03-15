package com.usp.items;

import java.io.*;
import java.net.*;

public class NeighborFilePeer implements NeighborPeer{
	private String address;
	private int port;
	private Status status;
	
	public NeighborFilePeer(String address, int port) {
		this.address = address;
		this.port = port;
		status = Status.OFFLINE;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setOnline() {
		status = Status.ONLINE;
		printUpdate(status);
	}
	
	public void setOffline() {
		status = Status.OFFLINE;
		printUpdate(status);
	}
	
	private void  printUpdate(Status status) {
		System.out.println("Atualizando peer " + getAddress() + ":" + getPort() + " status " + status);
	}
	
	public String connect(TypeMessages request) throws IOException{
		try(Socket socket = new Socket(address, port)){;
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			output.print("request");
			output.flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return input.readLine();
		}
	}
}
