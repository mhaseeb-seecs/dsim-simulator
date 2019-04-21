package com.haseeb.simulator;

import java.util.ArrayList;
import java.util.List;

public class MessagePool {
    List<Message> messages = new ArrayList<Message>();

    public void addMessage(Message msg) {
            messages.add(msg);
    }

    public int find(Message msg) {
        for(int i=0; i<messages.size(); i++) {
            if(messages.get(i).compareTo(msg) == 0)
                return i+1;
        }
        return 0;
    }

    public Boolean findOlderMessage(Message msg) {
        for(int i=0; i<messages.size(); i++) {
            if(messages.get(i).compareTo(msg) == -1 && messages.get(i).getStatus() == 0) {
                return true;
            }
        }
        return false;
    }

    public int countOlderMessages(long timestamp) {
        int totalMessages = 0;
        for(int i=0; i<messages.size(); i++) {
            if(messages.get(i).timeStamp > timestamp) {
                totalMessages++;
            }
        }
        return totalMessages;
    }


}
