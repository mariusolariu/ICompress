package com.mygdx.icompress;

import java.io.*;
import java.net.Socket;

/**
 * Created by Ronan
 * on 01/05/2017.
 */
public class ClientConnection implements Runnable{
    private Socket connection = null;
    private OutputStream writer = null;
    private InputStream reader = null;
    private File inputFile;
    private File outputFile;

    public ClientConnection(String host, int port, File inputFile, File outputFile){
        try {
            connection = new Socket(host, port);
            this.inputFile = inputFile;
            this.outputFile = outputFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run(){
        try {
            writer = connection.getOutputStream();
            reader = connection.getInputStream();
            byte[] bytes = new byte[16 * 1024];



            //We send the request to the server
            byte[] mybytearray = new byte[(int) inputFile.length()];

            FileInputStream fis = new FileInputStream(inputFile);
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
            OutputStream output = new FileOutputStream(outputFile);
            long size = clientData.readLong();
            System.out.println("read: " + size);
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
            {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }
            output.flush();

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
}
