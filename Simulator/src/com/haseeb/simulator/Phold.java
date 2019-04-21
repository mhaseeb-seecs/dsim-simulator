package com.haseeb.simulator;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Phold extends Thread{

    List<Worker> workers = new ArrayList<Worker>();
    int eventCount = 0;
    int processedEvents = 0;
    long simTime =0;

    //For RealTime Events
    /*
    List<SimEvents> simEvents = new SimEvents();
     */
    // OR Use Random Number of Events
    //For Generating Total Random Events
    int totalEvents = 2000;

    //Rollback Stats
    int rollbacks = 0;
    int totalRollbackedEvents = 0;
    MessagePool messagePool = new MessagePool();
    private Random randomGenerator;
    long startTime;
    long stopTime;
    int messagePerSec = 100;

    Phold() {
        randomGenerator = new Random();
    }

    public void registerWorker(String address, String name, Object port) {
        Worker worker = new Worker(address, name);
        int clientPort = Integer.parseInt((String)  port);
        worker.setClientPort(clientPort);
        workers.add(worker);
    }

    public void run () {
        startTime = System.currentTimeMillis();
        System.out.println("Simulation Started....");
        try {
            while (simRunning()) {
                sendMessage();
                Thread.sleep(1000/messagePerSec);
            }
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Connection Error");
        }
        simComplete();
    }

    public void sendMessage() {
        if(workers.isEmpty()) {
            //System.out.println("No Clients joined yet...");
            return;
        }

        if(eventCount >= totalEvents) {
            return;
        }

        Worker worker = getSuitableWorker();
        worker.sendMessage(String.valueOf(++eventCount), messagePool);
    }

    public void handleMessage(Socket socket) {
        //System.out.println("Handle New Message...");
        ConnectionHandler t = new ConnectionHandler(socket, this);
        t.start();
    }

    public void processMessage(Message msg) {
        System.out.println("Message Received:" + msg);
        if(msg.msgType == Message.MsgType.REGISTER) {
            this.registerWorker(msg.getSenderIP(), msg.getSenderName(),msg.content);
        } else if(msg.getMsgType() == Message.MsgType.SIM_RESULT) {
            int clientIndex = this.findWorker(msg.getSenderIP(), msg.getSenderName());
            if(clientIndex > 0)
                workers.get(clientIndex-1).processedMessage();
            this.processResult(msg);
        } else {
            messagePool.addMessage(msg);
        }
    }

    public void processResult(Message msg) {
        int i = messagePool.find(msg);
        if(i==0) {
            System.out.println("Unknown Message Found");
        } else {
            //Checking if need to rollback.
            mayBeRollBack(msg);
            System.out.println("Message Processed:"+messagePool.messages.get(i - 1));
            processedEvents++;
            messagePool.messages.get(i - 1).setStatus(1);
        }
    }

    public Boolean simRunning() {
        if(eventCount >= totalEvents && processedEvents >= totalEvents)
            return false;

        return true;
    }

    public Worker getSuitableWorker()
    {
        Boolean found = false;
        Worker worker = null;
        while(!found) {
            //For Randomization Algorithm Pick a random worker.
            int index = randomGenerator.nextInt(workers.size());
            //System.out.println("Clients:"+workers.size()+", Random:"+index);
            worker = workers.get(index);
            if (worker.canSendMessage())
                found = true;
        }
        return worker;
    }

    public void simComplete() {
        for(Worker c: workers){
            c.sendExitMessage(messagePool);
        }
        stopTime = System.currentTimeMillis();
        System.out.println("Simulation Completed...");
        System.out.println("Total Events: " + totalEvents);
        System.out.println("Processed Events: " + processedEvents);
        System.out.println("Rollbacks: " + rollbacks);
        System.out.println("Total Clients: " + workers.size());
        long elapsedTime = stopTime - startTime;
        System.out.println("Elapsed Time: " + elapsedTime + "ms");
        System.out.println("Events per Second: " + totalEvents/(elapsedTime/1000));
    }

    public void mayBeRollBack(Message msg) {
        Boolean oldMsgFound = messagePool.findOlderMessage(msg);
        if(oldMsgFound && simTime < msg.timeStamp) {
            //No Need to Rollback. Exclude the message.
        } else {
            rollback(msg.timeStamp);
        }
    }

    public void rollback(long timestamp) {
        simTime = timestamp;
        //Counting Total number of Events Rollback
        totalRollbackedEvents =  messagePool.countOlderMessages(timestamp);
        rollbacks++;
    }

    public int findWorker(String address, String name) {
        //System.out.println("Address:"+address+",Name:"+name);
        for(int i = 0; i< workers.size(); i++) {
            if(workers.get(i).getAddress().equals(address) && workers.get(i).getName().equals(name)) {
                //System.out.println("Found ==> Address:"+workers.get(i).getAddress()+",Name:"+workers.get(i).getName());
                return i+1;
            }
        }

        return 0;
    }
}
