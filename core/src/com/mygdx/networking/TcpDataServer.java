package com.mygdx.networking;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Created by Marius on 11.06.2017.
 */

public class TcpDataServer {

    public static void main(String[] args){
        if (args.length != 2){
            System.err.println("Usage: java Server <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        File lz4ArchivesDirectory = new File("Lz4 archives");
        File lz4UnarchivedFilesDirectory = new File("Lz4 unpacked files");
        File zipArchives = new File("Zip archives");

        if (!lz4ArchivesDirectory.exists()) lz4ArchivesDirectory.mkdir();
        if (!lz4UnarchivedFilesDirectory.exists()) lz4UnarchivedFilesDirectory.mkdir();
        if (!zipArchives.exists()) zipArchives.mkdir();
        String lz4A_Path = lz4ArchivesDirectory.getAbsolutePath();
        String lz4U_Path  = lz4UnarchivedFilesDirectory.getAbsolutePath();
        String zipA_Path = zipArchives.getAbsolutePath();

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber, 100, InetAddress.getByName(hostName));

                while (true){
                    System.out.println("Server listening on: " + hostName + ", port:" +portNumber);

                    Socket client = serverSocket.accept();
                    new Thread(new TcpClientWorker(client, lz4A_Path, lz4U_Path, zipA_Path)).start();

                }


        } catch (IOException e) {
            System.err.println("Don't know the host:" +hostName );
            e.printStackTrace();
        }
    }

}
