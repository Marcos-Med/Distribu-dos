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
				System.out.println("\nMensagem recebida:\"" + message + "\"");
				String[] args = message.split(" ");
				int clock = Integer.parseInt(args[1]); //clock recebido
				server.tick(clock);//altera o clock local
				String command = args[2]; //tipo de mensagem recebida
				String response = server.response(TypeMessages.valueOf(command), args); //processa a request
				if(response == null) response = "\n";  //Caso não haja resposta
				else {
					System.out.println("\nEncaminhando mensagem \"" + response.replace("\n", "") + "\" para " + args[0]);
				}
				System.out.print(">");
				output.print(response);//envia a resposta
				output.flush();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				socket.close();//fecha conexão
				
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
