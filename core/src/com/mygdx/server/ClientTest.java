package com.mygdx.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Created by Ronan on 06/06/2017.
 */

public class ClientTest implements Runnable{
    private String host;
    private int port;
    private Socket connection = null;
    private InputStream reader;
    private OutputStream writer;
    private InputStream in;
    private OutputStream out;
    private String inputPath = "C:\\Users\\Ronan\\Documents\\Travail\\Polytech\\4a\\Radboud\\" +
            "Analysis\\Homework\\ICompress\\ICompress\\test\\input\\";
    private String resultPath = "C:\\Users\\Ronan\\Documents\\Travail\\Polytech\\4a\\Radboud\\" +
            "Analysis\\Homework\\ICompress\\ICompress\\test\\result\\";

    public ClientTest(String host, int port) {
        try {
            sleep(2000);
            this.host = host;
            this.port = port;
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
            writer = connection.getOutputStream();
            reader = connection.getInputStream();
            byte[] bytes = new byte[16 * 1024];



            //We send the request to the server
            File file = new File(inputPath + "music.zip");
            byte[] mybytearray = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            OutputStream os = connection.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();

            //Sending file data to the server
            os.flush();
            System.out.println("I will send");
            int readLength;
            while ((readLength = bis.read(bytes)) > 0) {
                os.write(bytes, 0, readLength);
            }
            os.flush();
            System.out.println("Order sent to the server");

            //Save the data sent by the server
            //We create the new file where we will save the data
            InputStream in = connection.getInputStream();
            ObjectInputStream clientData = new ObjectInputStream(in);
            int bytesRead;
            File result = new File(resultPath + "result.zip");
            OutputStream output = new FileOutputStream(result);
            long size = clientData.readLong();
            System.out.println("read: " + size);
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
            {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }
            output.flush();
            in.close();

        } catch (IOException e1) {
            System.out.println("error !!!");
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
