package com.haseeb.simulator;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {

    static MessagePool messagePool = new MessagePool();
    static Boolean simRunning = true;
    static String serverAddress = "192.168.10.12";
    static Socket s1 = null;
    public static void main(String args[]) throws IOException {



        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        ServerSocket receiverSocket = null;
        Socket socket = null;
        Thread process = new Thread(processs);
        try {
            s1 = new Socket(serverAddress, 4445);
            oos = new ObjectOutputStream(s1.getOutputStream());
            receiverSocket = new ServerSocket(4446);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("IO Exception");
        }

        System.out.println("Client Name: " + InetAddress.getLocalHost().getHostName());
        System.out.println("Client Address: " + InetAddress.getLocalHost().getHostAddress());

        String response = null;
        try {
            Message msg = new Message(
                    0,
                    Message.MsgType.REGISTER,
                    "4446",
                    InetAddress.getLocalHost().getHostAddress(),
                    InetAddress.getLocalHost().getHostName(),
                    serverAddress
            );
            oos.writeObject(msg);
            process.start();
            while (simRunning) {
                socket = receiverSocket.accept();
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                handleMessage(ois);
            }
            process.interrupt();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Socket read Error");
        } finally {
            oos.close();
            ois.close();
            System.out.println("Connection Closed");
        }
    }

    public static void handleMessage(ObjectInputStream ois) {
        try {
            Message msg = (Message) ois.readObject();
            processMessage(msg);
            System.out.println(msg);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void processMessage(Message msg) {
        if(msg.getMsgType() == Message.MsgType.SIM_ENDSIM) {
            simRunning = false;
        }
        messagePool.addMessage(msg);
    }

    public static Runnable processs = new Runnable() {

        @Override
        public void run() {
            try {
                ObjectOutputStream oos = null;
                Boolean simRu = true;
                while (simRu) {
                    try {
                        for (int i = 0; i < messagePool.messages.size(); i++) {
                            if (messagePool.messages.get(i).getStatus() == 0 && messagePool.messages.get(i).getMsgType() == Message.MsgType.SIM_REGULAR) {
                                messagePool.messages.get(i).setStatus(1);
                                System.out.println("Proccessing Message: " + messagePool.messages.get(i));
                                s1 = new Socket(serverAddress, 4445);
                                oos = new ObjectOutputStream(s1.getOutputStream());
                                Message msg = new Message(
                                        messagePool.messages.get(i).getTimeStamp(),
                                        Message.MsgType.SIM_RESULT,
                                        messagePool.messages.get(i).getContent(),
                                        InetAddress.getLocalHost().getHostAddress(),
                                        InetAddress.getLocalHost().getHostName(),
                                        "192.168.1.100"
                                );
                                System.out.println("Sending Message: " + msg);
                                oos.writeObject(msg);
                            }
                        }
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupt Called");
                        oos.close();
                        s1.close();
                        simRu = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e ){
                e.printStackTrace();
            }
        }
    };
}
