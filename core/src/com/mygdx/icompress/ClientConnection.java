package com.mygdx.icompress;

import com.mygdx.archiveAlgorithm.Zip;
import com.mygdx.server.ZipObject;

import java.io.*;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Ronan
 * on 01/05/2017.
 */
public class ClientConnection implements Runnable{
    private Socket connection = null;
    private ObjectOutputStream writer = null;
    private ObjectInputStream reader = null;
    private ZipObject zipObject;

    public ClientConnection(String host, int port, ZipObject zipObject){
        this.zipObject = zipObject;
        try {
            connection = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run(){
        try {
            writer = new ObjectOutputStream(connection.getOutputStream());
            reader = new ObjectInputStream(connection.getInputStream());

            //We send the request to the server
            writer.writeObject(zipObject);
            //We use flush to let the flux open
            writer.flush();

            System.out.println("Order sent to the server");

            //We are waiting the reply
            ZipObject response = read();
            System.out.println("\t * Reply received " + response);

            // TODO: 05/06/2017 When the app receives the server's answer

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method to read the server's reply
    private ZipObject read() throws IOException{
        ZipObject result = null;
        try {
            result = (ZipObject)reader.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
