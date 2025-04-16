package com.usp.items;

import java.io.IOException;

public interface NeighborPeer { //Interface para peers conhecidos
	public String connect(TypeMessages request, String[] args) throws IOException;
	public String getAddress();
	public int getPort();
	public Status getStatus();
	public void setOnline();
	public void setOffline();
	public void printUpdate(Status status);
	public void printAddPeer(Status status);
	public int getClock();
	public void setClock(int clock);
}
