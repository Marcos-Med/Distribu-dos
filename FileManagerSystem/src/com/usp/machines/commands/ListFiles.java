package com.usp.machines.commands;


import java.io.File;

public class ListFiles implements Command{ //Comando para listar arquivos locais
	
	private String fileDirectory; //Diretório de arquivos
	
	public ListFiles(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}
	
	public void execute(String[] args) {
		File file = new File(fileDirectory);
		File[] files = file.listFiles(); //Retorna lista de arquivos
		if(files != null) {
			for(File f: files) {
				System.out.println(f.getName()); //imprime o nome de cada arquivo
			}
		}
		else System.out.println("Erro ao listar arquivos.");
	}
}
