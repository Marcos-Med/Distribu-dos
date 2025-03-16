package com.usp.machines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRequest implements Runnable{
	private Socket socket;
	
	public ClientRequest(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		try(BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			String message;
			while((message = input.readLine()) != null) {
				//...response
				output.print("....");
				output.flush();
			}
			socket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
