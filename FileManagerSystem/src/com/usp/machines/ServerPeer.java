package com.usp.machines;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import com.usp.items.FilePeer;

public class ServerPeer implements Runnable {
	private FilePeer peer;
	private volatile boolean running; //garante sincronização
	private ServerSocket serverSocket;
	
	public ServerPeer(FilePeer peer) {
		this.peer = peer;
		running = true;
	}
	
	public void off() { //para o sistema
		running = false;
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(peer.getPort());
			while(running) { //enquanto está ligado
				Socket socket = serverSocket.accept();
				peer.tick();
				new Thread(new ClientRequest(socket, peer)).start(); //trata requisições de peers clientes
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
