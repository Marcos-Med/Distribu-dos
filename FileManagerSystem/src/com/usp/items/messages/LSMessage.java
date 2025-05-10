package com.usp.items.messages;

import com.usp.items.TypeMessages;

public class LSMessage extends Message {
    public LSMessage() { super(); }
    
    public String buildMessage(String[] args) {
        String origin = args[0];
        String clock = args[1];
        TypeMessages type = TypeMessages.LS;
        return this.buildHead(origin, Integer.parseInt(clock), type) + "\n";
    }
}
