package com.usp.machines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.usp.items.FilePeer;
import com.usp.items.TypeMessages;

public class ClientRequest implements Runnable{ //Thread para atender requisições dos peers
	private Socket socket;
	FilePeer server;
	
	public ClientRequest(Socket socket, FilePeer server) {
		this.socket = socket;
		this.server = server;
		
	}
	
	public void run() {
		try(BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			String message;
			while((message = input.readLine()) != null) { //Enquanto há requisições
				String[] args = message.replace("\n", "").split(" ");
				String command = args[2]; //tipo de mensagem recebida
				String response = server.response(TypeMessages.valueOf(command), args); //processa a request
				if(response != null) { //Caso haja resposta
					output.print(response);//envia a resposta
					output.flush();
				}
			}
			socket.close();//fecha conexão
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
