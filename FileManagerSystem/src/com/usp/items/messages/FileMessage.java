package com.usp.items.messages;

import com.usp.items.TypeMessages;

public class FileMessage extends Message {
    public FileMessage() { super(); }
 
    public String buildMessage(String[] args) {
        String origin = args[0];
        String clock = args[1];
        String filename = args[2];
        String chunk1 = args[3];
        String chunk2 = args[4];
        String content = args[5];
        StringBuilder sb = new StringBuilder();
        sb.append(this.buildHead(origin, Integer.parseInt(clock), TypeMessages.FILE))
          .append(getSpace()).append(filename)
          .append(getSpace()).append(chunk1)
          .append(getSpace()).append(chunk2)
          .append(getSpace()).append(content)
          .append("\n");
        return sb.toString();
    }
}