package com.usp.items.messages;

import com.usp.items.TypeMessages;

public class LSListMessage extends Message {
    public LSListMessage() { super(); }
  
    public String buildMessage(String[] args) {
        String origin = args[0];
        String clock = args[1];
        int quantity = Integer.parseInt(args[2]);
        StringBuilder sb = new StringBuilder();
        sb.append(this.buildHead(origin, Integer.parseInt(clock), TypeMessages.LS_LIST))
          .append(getSpace()).append(quantity);
        for (int i = 0; i < quantity; i++) {
            sb.append(getSpace()).append(args[3 + i]);
        }
        sb.append("\n");
        return sb.toString();
    }
}
