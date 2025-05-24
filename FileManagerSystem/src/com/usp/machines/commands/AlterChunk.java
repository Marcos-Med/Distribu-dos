package com.usp.machines.commands;

import java.util.Scanner;
import com.usp.items.FilePeer;
import com.usp.machines.InterfacePeer;

public class AlterChunk implements Command{ //implementação da alteração de chunk
	private FilePeer peer;
	
	public AlterChunk(FilePeer peer) {
		this.peer = peer;
	}
	
	public void execute(String[] args) {
		System.out.println("Digite novo tamanho de chunk:");
		System.out.print(">");
		Scanner input = InterfacePeer.getInput();
		if(input.hasNextInt()) {
			int chunk = input.nextInt();
			if(chunk > 0) { //Somente são aceitos chunk positivos
				peer.setChunk(chunk);
				System.out.println("Tamanho de chunk alterado: " + peer.getChunk());
			}
			else System.out.println("Tamanho de chunk inválido!");
		} else input.nextLine();//Descarta entrada inválida
	}
}
