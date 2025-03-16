package com.usp.app;
import java.io.File;

import com.usp.machines.FileServer;
import com.usp.machines.InvalidParametersException;

public class EACHare { //programa principal
	public static void main(String[] args) {
		String[] params = EACHare.processArguments(args); //Valida os parâmetros
		String address = params[0]; //IP
		int port = Integer.parseInt(params[1]); //Porta
	    String neighborsTxt = params[2]; //vizinhos.txt
	    String fileDirectory = params[3]; //diretório de arquivos
		FileServer peer = new FileServer(address, port, neighborsTxt, fileDirectory);
		peer.run(); //Roda o servidor de arquivo
	}
	
	private static String[] processArguments(String[] args) throws InvalidParametersException{
		if(args.length != 3) throw new InvalidParametersException();
		String[] result = args[0].split(":"); //Divide a identificação <endereço>:<porta>
		if(result.length != 2) throw new InvalidParametersException();
		
        String address = result[0];
        String port = result[1];
        String neighborsTxt = args[1];
        String fileDirectory = args[2];
        
        //Se o arquivo existe
        File fileNeighbors = new File(neighborsTxt);
        if(!fileNeighbors.exists()) throw new InvalidParametersException();
        
        //Se é um diretório válido
        File directory = new File(fileDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new InvalidParametersException();
        }
        
        return new String[]{address, port, neighborsTxt, fileDirectory}; //retorna parâmetros
	}
}
