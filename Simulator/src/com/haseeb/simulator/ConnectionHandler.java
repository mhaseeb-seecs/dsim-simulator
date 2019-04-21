package com.haseeb.simulator;

import java.io.*;
import java.net.Socket;

class ConnectionHandler extends Thread {

    String line = null;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    Socket socket = null;
    Phold sim = null;
    Message msg = null;

    public ConnectionHandler(Socket s, Phold sim) {
        this.socket = s;
        this.sim = sim;
    }

    public void run() {
        try {

            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            System.out.println("IO error in server thread");
        }

        try {
            msg = (Message)ois.readObject();
            sim.processMessage(msg);
            //System.out.println(msg);
        } catch (IOException e) {
            line = this.getName(); //reused String line for getting thread name
            System.out.println("IO Error/ Worker " + line + " terminated abruptly");
        } catch (NullPointerException e) {
            line = this.getName(); //reused String line for getting thread name
            System.out.println("Worker " + line + " Closed");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                    //System.out.println("Socket Object Output Stream Closed");
                }
                if (ois != null) {
                    ois.close();
                    //System.out.println("Socket Object Input Stream Closed");
                }
                if (socket != null) {
                    socket.close();
                    //System.out.println("Socket Closed");
                }

            } catch (IOException ie) {
                System.out.println("Socket Close Error");
            }
        }//end finally
    }


}