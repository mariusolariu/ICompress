package com.mygdx.server;

import com.mygdx.archiveAlgorithm.UnZip;
import com.mygdx.archiveAlgorithm.Zip;

import java.io.EOFException;
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
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Ronan
 * on 01/05/2017.
 */
public class ClientProcessor implements Runnable{
    private Socket sock;
    private ObjectOutputStream writer = null;
    private ObjectInputStream reader = null;
    private boolean isRunning = true;

    public ClientProcessor(Socket pSock){
        sock = pSock;
    }

    //We treat the requests in another thread
    public void run(){
        System.out.println("Launching client connection processing");

        //As long as the connection is active, we treat the requests
        while(!sock.isClosed()) try {

            writer = new ObjectOutputStream(sock.getOutputStream());
            reader = new ObjectInputStream(sock.getInputStream());

            //We are waiting the client's request
            //TODO change type
            ZipObject order = read();

            writer.writeBoolean(true);
            writer.flush();

            InetSocketAddress remote = (InetSocketAddress) sock.getRemoteSocketAddress();

            //We display some information for the debug
            String debug;
            debug = "Thread : " + Thread.currentThread().getName() + ". ";
            debug += "Request the address : " + remote.getAddress().getHostAddress() + ".";
            debug += " On the port : " + remote.getPort() + ". ";
            debug += "\t -> Order received : " + order.toString() + "\n";

            System.out.println(debug);

            //Files -> Archive
            if (order.getIWantToZip()) {
                Zip zipper = new Zip();
                zipper.archiveFiles(order.getArchiveFiles(), "", order.getArchiveName());
                System.out.println("Zip");
            }
            //Archive -> Files
            else {
                File file = new File("C:\\Users\\Ronan\\Documents\\Travail\\Polytech\\4a\\Radboud\\Analysis\\Homework\\ICompress\\ICompress\\test\\output\\"+order.getArchiveName()+".zip");
                byte[] bytes = new byte[16 * 1024];
                InputStream in = sock.getInputStream();
                OutputStream out = new FileOutputStream(file);

                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                out.flush();

                UnZip unZipper = new UnZip();
                ArrayList<File> archiveList = new ArrayList<File>();
                archiveList.add(file);

                File theDir = new File("C:\\Users\\Ronan\\Documents\\Travail\\Polytech\\4a\\Radboud\\Analysis\\Homework\\ICompress\\ICompress\\test\\output\\" + order.getArchiveName());

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

                unZipper.unarchiveFiles(archiveList, theDir.getPath());

                for (File fileEntry: theDir.listFiles()) {
                    in = new FileInputStream(fileEntry);
                    out = sock.getOutputStream();
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }
                }


                System.out.println("Unzip");
            }
            //TODO create the result


            //We send the reply to the client

            System.out.println("Object sent to the client.");

            //WE MUST USE flush()
            //Otherwise the data will not be transmitted
            //to the client and it will wait indefinitely
            writer.flush();
            writer.close();
            reader.close();
        } catch (SocketException e) {
            System.err.println("THE CONNECTION IS INTERRUPTED ! ");
            break;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isRunning = false;
        }
    }

    //Method to read the client's reply
    private ZipObject read() throws IOException {
        Object result = null;
        try {
            result = reader.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (ZipObject) result;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
