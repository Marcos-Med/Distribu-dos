package com.usp.machines;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
	private String address;
	private int port;
	private boolean running;
	private int clock;
	private String filePeers;
	private String fileDirectory;
	
	public FileServer(String address, int port, String FilePeers, String directory) {
		this.address = address;
		this.port = port;
		clock = 0;
		running = true;
		filePeers = FilePeers;
		fileDirectory = directory;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getClock() {
		return clock;
	}
	
	public void increaseClock() {
		clock++;
	}
	
	public synchronized void off() {
		running = false;
	}
	
	public void run() {
		
		new Thread(new InterfacePeer(address, port, filePeers, fileDirectory, this)).start();
		
		try(ServerSocket serverSocket = new ServerSocket(port)){
			while(running) {
				Socket socket = serverSocket.accept();
				new Thread(new ClientRequest(socket)).start();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
