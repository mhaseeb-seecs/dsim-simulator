package com.haseeb.simulator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MessagePool {
    List<Message> messages = new ArrayList<Message>();

    public void addMessage(Message msg) {
        messages.add(msg);
    }

    public Message getUnprocessedMessage() {
        for(Message msg: messages) {
            if(msg.status == 0 && msg.msgType == Message.MsgType.SIM_REGULAR) {
                return msg;
            }
        }
        return null;
    }

    public void setMessageStatus(int index, int status) {

    }


}
