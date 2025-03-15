package com.usp.app;
import com.usp.machines.FileServer;

public class EACHare {
	public static void main(String[] args) {
		String address;
		int port;
		if(args.length != 3) { //Invalid Parameters
			address = "";
			port = -1;
		}
		else {
			String[] result = args[0].split(":");
			address = result[0];
			port = Integer.parseInt(result[1]);
		}
		FileServer peer = new FileServer(address, port);
		peer.run();
	}
}
