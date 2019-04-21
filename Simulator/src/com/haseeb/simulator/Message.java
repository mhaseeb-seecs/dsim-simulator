package com.haseeb.simulator;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.Time;

public class Message implements Serializable, Comparable<Message> {

    public enum MsgType {
        SIM_REGULAR,
        SIM_ANTI,
        SIM_STRAGGLER,
        SIM_RESULT,
        SYSTEM_ACKNOWLEDGE,
        SIM_ENDSIM,
        REGISTER,
        SOCK_CLOSE,
        HEARTBEAT
    }

    MsgType msgType;
    long timeStamp;
    Object content;
    String senderName;
    String senderIP;
    String destination;
    int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderIP() {
        return senderIP;
    }

    public void setSenderIP(String senderIP) {
        this.senderIP = senderIP;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Message(long time, MsgType typ, Object cont, String senderIP, String senderName, String destination)
    {
        this.timeStamp= time;
        this.msgType= typ;
        this.content= cont;
        this.senderName = senderName;
        this.senderIP = senderIP;
        this.destination = destination;

    }//end constructor

    public String toString()
    {
        return "Message of type "+msgType+" with timestamp "+timeStamp+
                " from ("+senderName+","+senderIP+") to ("+destination+") with content ("+content+")";
    }

    public int compareTo(Message O)
    {
        if (timeStamp < O.timeStamp)
            return -1;
        else if (timeStamp > O.timeStamp)
            return 1;
        return 0;
    }

}
