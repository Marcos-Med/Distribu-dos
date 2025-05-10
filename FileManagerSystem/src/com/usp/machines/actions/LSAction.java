package com.usp.machines.actions;

import com.usp.items.BuilderMessage;
import com.usp.items.FilePeer;
import com.usp.items.NeighborFilePeer;
import com.usp.items.NeighborPeer;
import com.usp.items.Status;
import com.usp.items.TypeMessages;
import java.io.File;
import java.util.List;

public class LSAction implements Action {
    private String directory;
    private BuilderMessage builder;
    private List<NeighborPeer> list;

    public LSAction(String directory, List<NeighborPeer> list) {
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
        File dir = new File(directory); //abre diretório
        File[] files = dir.listFiles();
        int qty = files != null ? files.length : 0; //quantidade de arquivos
        String[] msgArgs = new String[3 + qty];
        msgArgs[0] = args[0];
        msgArgs[1] = Integer.toString(FilePeer.getInstance().tick());
        msgArgs[2] = Integer.toString(qty);
        for (int i = 0; i < qty; i++) {
            msgArgs[3 + i] = files[i].getName() + ":" + files[i].length(); //prenche com os nomes dos arquivos
        }
        return builder.buildMessage(TypeMessages.LS_LIST, msgArgs);
    }
}