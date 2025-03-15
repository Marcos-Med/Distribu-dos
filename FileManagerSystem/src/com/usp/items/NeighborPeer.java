package com.usp.items;

import java.io.IOException;

public interface NeighborPeer {
	public String connect(TypeMessages request) throws IOException;
	public String getAddress();
	public int getPort();
	public Status getStatus();
	public void setOnline();
	public void setOffline();
}
