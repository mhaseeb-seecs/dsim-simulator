package com.haseeb.simulator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String args[]){
        Socket socket = null;
        ServerSocket serverSocket=null;

        Phold sim = new Phold();

        try {
            serverSocket = new ServerSocket(4445);
            }
            catch(IOException e){
                e.printStackTrace();
                System.out.println("Error while starting Server...");

            }
            System.out.println("Waiting for the Connections...");
            sim.start();
            while(sim.isAlive()){
                try{
                    socket = serverSocket.accept();
                    //System.out.println("Established a connection with client...");
                    //System.out.println("Address: " + socket.getRemoteSocketAddress().toString());
                    //System.out.println("IPAddress: " + socket.getInetAddress().toString().replace("/",""));
                    sim.handleMessage(socket);
                }

                catch(Exception e){
                e.printStackTrace();
                System.out.println("Connection Error");
            }
        }

    }

}

