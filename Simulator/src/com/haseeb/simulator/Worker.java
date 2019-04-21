package com.haseeb.simulator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Worker {

    private String address;
    private String name;
    private int eventsPending = 0;
    private int eventsExecuted;
    private int maxEventsQueue = 1;
    private int totalEvents;
    private int status;
    private int clientPort = 4446;
    private Socket socket = null;
    private ObjectOutputStream oos = null;

    Worker(String address, String name) {
        this.setAddress(address);
        this.setName(name);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEventsPending() {
        return eventsPending;
    }

    public void setEventsPending(int eventsPending) {
        this.eventsPending = eventsPending;
    }

    public int getEventsExecuted() {
        return eventsExecuted;
    }

    public void setEventsExecuted(int eventsExecuted) {
        this.eventsExecuted = eventsExecuted;
    }

    public int getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(int totalEvents) {
        this.totalEvents = totalEvents;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMaxEventsQueue() {
        return maxEventsQueue;
    }

    public void setMaxEventsQueue(int maxEventsQueue) {
        this.maxEventsQueue = maxEventsQueue;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public Boolean sendMessage(Object content, MessagePool pool) {
        //System.out.println("Sending Message to Worker ("+this.getName()+","+this.getAddress()+")");
        try {
            socket = new Socket(this.getAddress(), clientPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Connection Error: Message Could not be sent ("+this.getName()+","+this.getAddress()+")");
            return false;
        }

        try {
            Message msg = new Message(
                    System.currentTimeMillis(),
                    Message.MsgType.SIM_REGULAR,
                    content,
                    InetAddress.getLocalHost().getHostName(),
                    InetAddress.getLocalHost().getHostAddress(),
                    this.getAddress()
            );
            pool.addMessage(msg);
            oos.writeObject(msg);
            eventsPending++;
            System.out.println("Message Sent: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connection Error: Message Could not be sent ("+this.getName()+","+this.getAddress()+")");
            return false;
        }

        return true;
    }

    public void sendExitMessage(MessagePool pool) {
        System.out.println("Sending Exist Message to Worker ("+this.getName()+","+this.getAddress()+")");

        try {
            socket = new Socket(this.getAddress(), clientPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Connection Error: Message Could not be sent ("+this.getName()+","+this.getAddress()+")");
        }


        try {
            Message msg = new Message(
                    0,
                    Message.MsgType.SIM_ENDSIM,
                    "Exit",
                    InetAddress.getLocalHost().getHostName(),
                    InetAddress.getLocalHost().getHostAddress(),
                    this.getAddress()
            );
            //pool.addMessage(msg);
            oos.writeObject(msg);
        } catch (IOException e) {
            System.out.println("Connection Error: Message Could not be sent ("+this.getName()+","+this.getAddress()+")");
        }
    }

    public void heartbeat() {
        try {
            Message msg = new Message(
                    System.currentTimeMillis(),
                    Message.MsgType.HEARTBEAT,
                    "Message Time: " +  System.currentTimeMillis(),
                    InetAddress.getLocalHost().getHostName(),
                    InetAddress.getLocalHost().getHostAddress(),
                    this.getAddress()
            );
            //pool.addMessage(msg);
            oos.writeObject(msg);
            eventsPending++;
            System.out.println("Message Sent: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connection Error: Message Could not be sent ("+this.getName()+","+this.getAddress()+")");
        }
    }

    public Boolean canSendMessage() {
        if(getEventsPending() >= getMaxEventsQueue())
            return false;

        return true;
    }

    public int nodeEfficiency() {

        return 1;
    }

    public void processedMessage() {
        eventsExecuted++;
        eventsPending--;
    }

}
