package com.mygdx.server;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import static java.lang.Thread.sleep;

/**
 * Created by Ronan
 * on 01/05/2017.
 */
public class Server {

    //We initialize default values
    private int port = 2345;
    private String host = "127.0.0.1";
    private ServerSocket server = null ;
    private boolean isRunning = true;

    //Default constructor
    public Server(){
        try {
            server = new ServerSocket(port, 100, InetAddress.getByName(host));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(String pHost, int pPort){
        host = pHost;
        port = pPort;

        try {
            server = new ServerSocket(port, 100, InetAddress.getByName(host));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //We launch the server
    public void open(){
        //We use a thread
        Thread thread = new ServerThread(server);
    }

    public void close(){
        isRunning = false;
        System.out.println("The server is closed now...");
    }


    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 2345;

        Server ts = new Server(host, port);
        ts.open();
        System.out.println("Server initialized on port: " + port + ", IP address: " + host);

        while (ts.isRunning){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
