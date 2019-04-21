package com.haseeb.simulator;

public class ProcessEvents extends Thread{

    MessagePool pool = null;
    Boolean simRunning = true;
    ProcessEvents(MessagePool pool) {
        this.pool = pool;
    }

    public void run() {
        while(simRunning) {
            Message msg = this.pool.getUnprocessedMessage();
            if(msg != null) {
                System.out.println("Unprocessed Message: " + msg);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void end() {
        simRunning = false;
    }
}
