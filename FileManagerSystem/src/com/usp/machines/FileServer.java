package com.usp.machines;

public class FileServer {
	private String address;
	private int port;
	
	public FileServer(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public void run() throws Exception{
		
	}
}
