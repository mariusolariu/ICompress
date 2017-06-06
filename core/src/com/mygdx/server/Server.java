package com.mygdx.server;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Created by Ronan
 * on 01/05/2017.
 */
public class Server implements Runnable{

    //We initialize default values
    private int port = 2345;
    private String host = "192.168.0.103";
    private ServerSocket serverSocket = null ;
    private boolean isRunning = true;

    //Default constructor
    public Server(){
        try {
            serverSocket = new ServerSocket(port, 100, InetAddress.getByName(host));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(String pHost, int pPort){
        host = pHost;
        port = pPort;

        try {
            serverSocket = new ServerSocket(port, 100, InetAddress.getByName(host));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //We launch the serverSocket
    public void open(){
        while (isRunning){
            try {
                System.out.println("Server initialize, port: " + port + " IP address: " + host);
                //We are waiting a client's connection
                Socket client = serverSocket.accept();

                //When we received a request, we treat it in another thread
                System.out.println("Client connection received.");
                Thread requestThread = new Thread(new ClientProcessor(client));
                requestThread.start();
                while (requestThread.isAlive()){
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void close(){
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("The serverSocket is closed now...");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public static void main(String[] args) {
        String host = "192.168.0.103";
        int port = 2345;

        Server ts = new Server(host, port);
        ts.open();
    }

    @Override
    public void run() {
        open();
    }
}
