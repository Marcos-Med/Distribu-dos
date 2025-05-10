package com.usp.items.messages;

import com.usp.items.TypeMessages;

public class DLMessage extends Message {
    public DLMessage() { super(); }

    
    public String buildMessage(String[] args) {
        String origin = args[0];
        String clock = args[1];
        String filename = args[2];
        String chunk1 = args[3];
        String chunk2 = args[4];
        return this.buildHead(origin, Integer.parseInt(clock), TypeMessages.DL)
            + getSpace() + filename
            + getSpace() + chunk1
            + getSpace() + chunk2
            + "\n";
    }
}
