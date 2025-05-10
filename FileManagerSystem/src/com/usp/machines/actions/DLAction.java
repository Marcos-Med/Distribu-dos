package com.usp.machines.actions;

import com.usp.items.BuilderMessage;
import com.usp.items.FilePeer;
import com.usp.items.NeighborFilePeer;
import com.usp.items.NeighborPeer;
import com.usp.items.Status;
import com.usp.items.TypeMessages;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;


public class DLAction implements Action {
    private String directory;
    private BuilderMessage builder;
    private List<NeighborPeer> list;

    public DLAction(String directory, List<NeighborPeer> list) {
        this.directory = directory;
        this.builder = BuilderMessage.getInstance();
        this.list = list;
    }
    
    public String response(String[] args) {
    	String[] result = args[0].split(":"); //<endereço>:<porta>
		String address = result[0]; //IP
		int port = Integer.parseInt(result[1]); //porta
		int clock = Integer.parseInt(args[1]);//clock
		boolean notExists = true;
    	for(int i = 0; i < list.size(); i++) {
			NeighborPeer peer = list.get(i);
			if(peer.getAddress().equals(address) && peer.getPort() == port) {
				peer.setOnline(); //atualiza status
				peer.printUpdate(Status.ONLINE);
				peer.setClock(clock);//altera clock
				notExists = false; //peer existe na lista
				break;
			}
		}
		if(notExists) { //Adicionando peer novo a lista
			NeighborPeer peer = new NeighborFilePeer(address, port); //cria peer novo
			peer.setOnline();
			peer.printAddPeer(Status.ONLINE);
			peer.setClock(clock);//altera clock
			list.add(peer); //adiciona na lista
		}
        String filename = args[3];
        File file = new File(directory, filename); //abre diretório
        byte[] data;
        try (FileInputStream fis = new FileInputStream(file)) { //leitura de arquive em Bytes
            data = fis.readAllBytes();
        } catch (IOException e) {
            data = new byte[0];
        }
        String encoded = Base64.getEncoder().encodeToString(data); //codificação 64
        String[] msgArgs = new String[6];
        msgArgs[0] = args[0];
        msgArgs[1] = Integer.toString(FilePeer.getInstance().tick());
        msgArgs[2] = filename;
        msgArgs[3] = args[4];
        msgArgs[4] = args[5];
        msgArgs[5] = encoded;
        return builder.buildMessage(TypeMessages.FILE, msgArgs);
    }
}