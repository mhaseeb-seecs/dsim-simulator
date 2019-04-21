package com.haseeb.simulator;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends Thread{

    private MessagePool messagePool = new MessagePool();
    private Boolean simRunning = true;
    private String serverAddress;
    private Socket s1 = null;
    private String clientName;
    private int clientPort;
    private String clientIP;
    Client(String cname, int cport, String serverIP) {
        clientName = cname;
        clientPort = cport;
        serverAddress = serverIP;

    }

    public void run(){
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        ServerSocket receiverSocket = null;
        Socket socket = null;
        clientIP = Utils.getIPAddress(true);
        Thread process = new Thread(processs);
        try {
            s1 = new Socket(serverAddress, 4445);
            oos = new ObjectOutputStream(s1.getOutputStream());
            Log.d("SIMULATOR_DEBUG","Client Port: " + clientPort);
            receiverSocket = new ServerSocket(clientPort);
            Log.d("SIMULATOR_DEBUG","Client Name: " + clientName);
            Log.d("SIMULATOR_DEBUG","Client Address: " + clientIP);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("SIMULATOR_DEBUG","IO Exception");
        }

        String response = null;
        try {
            Message msg = new Message(
                    0,
                    Message.MsgType.REGISTER,
                    ""+clientPort,
                    clientIP,
                    clientName,
                    serverAddress
            );
            oos.writeObject(msg);
            //oos.close();
            process.start();
            while (simRunning) {
                socket = receiverSocket.accept();
                ois = new ObjectInputStream(socket.getInputStream());
                handleMessage(ois);
            }
            process.interrupt();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("SIMULATOR_DEBUG","Socket read Error");
        } finally {
            try {
                ois.close();
                receiverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SIMULATOR_DEBUG","Socket Close Error");
            }

            Log.d("SIMULATOR_DEBUG","Connection Closed");
        }
    }

    public  void handleMessage(ObjectInputStream ois) {
        try {
            Message msg = (Message) ois.readObject();
            processMessage(msg);
            System.out.println(msg);
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void processMessage(Message msg) {
        if(msg.getMsgType() == Message.MsgType.SIM_ENDSIM) {
            simRunning = false;
        }
        messagePool.addMessage(msg);
    }

    public Runnable processs = new Runnable() {

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
                                Log.d("SIMULATOR_DEBUG","Proccessing Message: " + messagePool.messages.get(i));
                                s1 = new Socket(serverAddress, 4445);
                                oos = new ObjectOutputStream(s1.getOutputStream());
                                Message msg = new Message(
                                        messagePool.messages.get(i).getTimeStamp(),
                                        Message.MsgType.SIM_RESULT,
                                        messagePool.messages.get(i).getContent(),
                                        clientIP,
                                        clientName,
                                        "192.168.1.100"
                                );
                                Log.d("SIMULATOR_DEBUG","Sending Message: " + msg);
                                oos.writeObject(msg);
                            }
                        }
                        Thread.sleep(1);
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
