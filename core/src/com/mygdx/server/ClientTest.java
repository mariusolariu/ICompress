package com.mygdx.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Thread.sleep;

/**
 * Created by Ronan on 06/06/2017.
 */

public class ClientTest implements Runnable{
    private Socket connection = null;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    public ClientTest(String host, int port) {
        try {
            sleep(2000);
            connection = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            writer = new ObjectOutputStream(connection.getOutputStream());
            reader = new ObjectInputStream(connection.getInputStream());

            File file = new File("C:\\Users\\Ronan\\Documents\\Travail\\Polytech\\4a\\Radboud\\Analysis\\Homework\\ICompress\\ICompress\\test\\input\\music.zip");

            ArrayList<File> list = new ArrayList<File>();
            list.add(file);

            ZipObject zipObject = new ZipObject(false, list, "music");

            //We send the request to the server
            writer.writeObject(zipObject);
            //We use flush to let the flux open
            writer.flush();

            System.out.println("Order sent to the server");

            //We are waiting the reply
            boolean response = reader.readBoolean();
            if (response){
                System.out.println("Confirmation");

                // Get the size of the file
                long length = file.length();
                byte[] bytes = new byte[16 * 1024];
                InputStream in = new FileInputStream(file);
                OutputStream out = connection.getOutputStream();

                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                out.flush();

                in = connection.getInputStream();
                File result = new File("C:\\Users\\Ronan\\Documents\\Travail\\Polytech\\4a\\Radboud\\Analysis\\Homework\\ICompress\\ICompress\\test\\result\\music");
                if (!result.exists()) {
                    System.out.println("creating directory: " + result.getName());
                    boolean b = false;
                    try {
                        result.mkdir();
                        b = true;
                    } catch (SecurityException se) {
                        System.err.println(Arrays.toString(se.getStackTrace()));
                    }
                    if (b) {
                        System.out.println("DIR created");
                    }
                }

                out = new FileOutputStream(result);
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
            }
            else {
                System.err.println("Error");
            }
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

    public static void main(String[] args){
        String host = "127.0.0.2";
        int port = 2345;

        Thread ts = new Thread(new Server(host, port));
        ts.start();

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Client launched");
        Thread t = new Thread(new ClientTest(host, port));
        t.start();

    }
}
