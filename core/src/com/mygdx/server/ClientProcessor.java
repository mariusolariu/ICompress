package com.mygdx.server;

import com.mygdx.archiveAlgorithm.UnZip;
import com.mygdx.archiveAlgorithm.Zip;

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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ronan
 * on 01/05/2017.
 */
public class ClientProcessor implements Runnable{
    private Socket sock;
    private OutputStream writer = null;
    private InputStream reader = null;
    private InputStream in;
    private OutputStream out;
    private boolean isRunning = true;
    private String outPath = "C:\\Users\\Ronan\\Documents\\Travail\\Polytech\\4a\\Radboud\\" +
            "Analysis\\Homework\\ICompress\\ICompress\\test\\output\\";
    private String inPath = "C:\\Users\\Ronan\\Documents\\Travail\\Polytech\\4a\\Radboud\\" +
            "Analysis\\Homework\\ICompress\\ICompress\\test\\input\\";

    public ClientProcessor(Socket pSock){
        sock = pSock;
    }

    //We treat the requests in another thread
    public void run() {
        System.out.println("Launching client connection processing");

        //As long as the connection is active, we treat the requests
        while (!sock.isClosed()) try {

            //Initialize our streams which will make the communication between the mobile and the server
            writer = sock.getOutputStream();
            reader = sock.getInputStream();


            //We display some information for the debug
            InetSocketAddress remote = (InetSocketAddress) sock.getRemoteSocketAddress();
            String debug;
            debug = "Thread : " + Thread.currentThread().getName() + ". ";
            debug += "Request the address : " + remote.getAddress().getHostAddress() + ".";
            debug += " On the port : " + remote.getPort() + ".";
            System.out.println(debug);

            //We create the new file where we will save the data
            File file = new File(outPath + "temp.zip");
            InputStream in = sock.getInputStream();

            DataInputStream clientData = new DataInputStream(in);

            int bytesRead;
            String fileName = outPath + "temp.zip";
            OutputStream output = new FileOutputStream(fileName);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }
            output.flush();

            System.out.println("Archive downloaded from the mobile");

            //We create the folder where we will save the extracted files
            File theDir = new File(outPath + "temp");
            if (!theDir.exists()) {
                System.out.println("creating directory: " + theDir.getName());
                boolean result = false;
                try {
                    theDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    System.err.println(Arrays.toString(se.getStackTrace()));
                }
                if (result) {
                    System.out.println("DIR created");
                }
            }

            //We create our UnZip Object
            UnZip unZipper = new UnZip();
            ArrayList<File> archiveList = new ArrayList<File>();
            archiveList.add(file);
            //We unzipped the archive
            unZipper.unarchiveFiles(archiveList, theDir.getPath());
            System.out.println("Unzipped");

            //TODO convert the files into a .rar file to send it to the client

            //We define the .rar we will send
            File toSend = new File(inPath + "toSend.rar");
            byte[] mybytearray = new byte[(int) toSend.length()];

            FileInputStream fis = new FileInputStream(toSend);
            BufferedInputStream bis = new BufferedInputStream(fis);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            OutputStream os = sock.getOutputStream();

            //Sending file name and file size to the client
            ObjectOutputStream dos = new ObjectOutputStream(os);
            System.out.println("write");
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();

            //Sending file data to the client
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            System.out.println("Object sent to the client.");

        } catch (SocketException e) {
            System.out.println("The client disconnected");
            break;
        }catch (IOException e) {
        e.printStackTrace();
        } finally {
            isRunning = false;
        }

        //WE MUST USE flush()
        //Otherwise the data will not be transmitted
        //to the client and it will wait indefinitely
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isRunning() {
        return isRunning;
    }
}
