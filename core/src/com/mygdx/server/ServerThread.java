package com.mygdx.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Ronan on 05/06/2017.
 */

public class ServerThread extends Thread {

    private ServerSocket server;

    public ServerThread(ServerSocket serverSocket) {
        this.server = serverSocket;
    }

    @Override
    public void run() {
        try {
            //We are waiting a client's connection
            Socket client = server.accept();

            //When we received a request, we treat it in another thread
            System.out.println("Client connection received.");
            Thread requestThread = new Thread(new ClientProcessor(client));
            requestThread.start();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
            server = null;
        }
    }
}
