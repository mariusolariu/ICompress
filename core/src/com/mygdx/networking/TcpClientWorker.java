package com.mygdx.networking;

import com.mygdx.archiveAlgorithm.Lz4Unpack;
import com.mygdx.archiveAlgorithm.ZipNativeLibrary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Marius on 11.06.2017.
 */

public class TcpClientWorker implements Runnable {

    //each  packet contains at maximum READ_LIMIT bytes
    private static final int READ_LIMIT = 8192;

    //communication
    private Socket client;
    private DataInputStream din;
    private DataOutputStream dout;

    //logic
    //receive file
    private RandomAccessFile fileToReceive_ROA;
    private String fileToReceiveName;
    private String fileToSendName;
    private long fileToReceiveLength;
    private long fTR_currentFilePointer;

    //send file
    private RandomAccessFile fileToSend_ROA;
    private long fileToSendPointerPosition;

    private boolean communicationNotFinished = true;
    private byte[] OPTION_123;
    private byte[] OPTION_125;
    private byte[] OPTION_128;
    private byte[] OPTION_130;
    private byte[] OPTION_131;
    private String lz4ArchivesFolderPath;
    private String lz4UnarchivedFilesFolderPath;
    private String zipFilesFolderPath;

    public TcpClientWorker(Socket client, String lz4ArchivesFolderPath, String lz4UnarchivedFilesFolderPath, String zipFilesFolderPath) {
        this.client = client;
        this.lz4ArchivesFolderPath = lz4ArchivesFolderPath;
        this.lz4UnarchivedFilesFolderPath = lz4UnarchivedFilesFolderPath;
        this.zipFilesFolderPath = zipFilesFolderPath;

        try {
            din = new DataInputStream(client.getInputStream());
            dout = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            System.err.println("I/O error when trying to bind the communication stream to dout/din in ClientWorker");
            e.printStackTrace();
        }

        try {
            OPTION_123 = "123".getBytes("UTF8");
            OPTION_125 = "125".getBytes("UTF8");
            OPTION_128 = "128".getBytes("UTF8");
            OPTION_130 = "130".getBytes("UTF8");
            OPTION_131 = "131".getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            while (communicationNotFinished) {

                //decoding the command, namely one of the options: 124, 126, 127
                byte[] cmd_buff = new byte[3];
                din.read(cmd_buff, 0, cmd_buff.length);

                byte[] informationBytes = readStream();

                int option = Integer.parseInt(new String(cmd_buff));

                communicateWithClient(option, informationBytes);

            }

            System.out.println("The file <<" + fileToSendName + ">> was sent successfully!");

            dout.flush();
            din.close();
            dout.close();
            client.close();

        } catch (IOException e) {
            System.err.println("I/O Error in TcpClientWorker");
            e.printStackTrace();
        }


    }


    private byte[] readStream() {
        byte[] data_buff = null;

        try {
            int byteRead = 0;
            String length_buff = "";


            while ((byteRead = din.read()) != '\\') {
                length_buff += (char) byteRead;
            }

            int totalBytesToRead = Integer.parseInt(length_buff);
            data_buff = new byte[totalBytesToRead];
            int byte_read;
            int byte_offset = 0;

            while (byte_offset < totalBytesToRead) {
                byte_read = din.read(data_buff, byte_offset, totalBytesToRead - byte_offset);
                byte_offset += byte_read;
            }

        } catch (IOException e) {
            System.err.println("Error in TcpClientWorker");
            e.printStackTrace();
        }


        return data_buff;
    }


    private byte[] createDataPacket(byte[] cmd, byte[] data) {
        byte[] packet = null;

        try {
            byte[] separator = new byte[1];
            separator[0] = '\\';

            byte[] lengthOfMessage = String.valueOf(data.length).getBytes("UTF8");

            //the standard form of a packet contains the bytes using the format from below
            packet = new byte[cmd.length + lengthOfMessage.length + separator.length + +data.length];

            //create packet
            System.arraycopy(cmd, 0, packet, 0, cmd.length);
            System.arraycopy(lengthOfMessage, 0, packet, cmd.length, lengthOfMessage.length);
            System.arraycopy(separator, 0, packet, cmd.length + lengthOfMessage.length, separator.length);
            System.arraycopy(data, 0, packet, cmd.length + lengthOfMessage.length + separator.length, data.length);


        } catch (UnsupportedEncodingException e) {
            System.err.println("Couldn't conver the length of array data[] in bytes");
            e.printStackTrace();
        }

        return packet;
    }

    private void communicateWithClient(int opt, byte[] informationBytes) {

        try {
            switch (opt) {

                //creates the server side file and sends an empty message (waiting for the next message containing fileSize)
                case 122:
                    fileToReceiveName = new String(informationBytes);

                    //it doesn't exist "w" option so in order to write you must create the file with "rw" option
                    fileToReceive_ROA = new RandomAccessFile(lz4ArchivesFolderPath + "/" + fileToReceiveName, "rw");

                    //it doesn't matter the message send because the next thing that the server is going to send is fileLength in bytes
                    dout.write(createDataPacket(OPTION_123, "send the length of the file".getBytes("UTF8")));
                    dout.flush();

                    break;

                case 124:
                    fileToReceiveLength = Long.valueOf(new String(informationBytes));

                    dout.write(createDataPacket(OPTION_125, String.valueOf(fTR_currentFilePointer).getBytes("UTF8")));
                    dout.flush();

                    break;

                //constantly asks for the next packet of data to be written until the server says that has sent everything
                case 126:

                    //update the writing position: position += (old)currentFilePointer; initially will set the pos to 0 (nothing was written before)
                    fileToReceive_ROA.seek(fTR_currentFilePointer);
                    fileToReceive_ROA.write(informationBytes);

                    //a temp variable
                    fTR_currentFilePointer = fileToReceive_ROA.getFilePointer();
                    float percentage = ((float) fTR_currentFilePointer / fileToReceiveLength) * 100;

                    if ((int) percentage == 100) {
                        System.out.println("ServerSide: Downloaded " + percentage + "%" + " of " + fileToReceiveName);
                    }


                    //asks for the next data packet providing the position from where to read
                    dout.write(createDataPacket(OPTION_125, String.valueOf(fTR_currentFilePointer).getBytes("UTF8")));
                    dout.flush();

                    break;

                //uncompress lz4, create zip, send the file length;
                case 127:
                    //unpack lz4 file -> one tar file
                    File receivedFile = new File(lz4ArchivesFolderPath + "/" + fileToReceiveName);
                    Lz4Unpack.unpackFile(receivedFile, lz4UnarchivedFilesFolderPath);
                    File dir = new File(lz4UnarchivedFilesFolderPath);

                    ArrayList<File> tempArrayList = new ArrayList<>();

                    //there will be only one file in this directory, the tar representing the .lz4 unpacked file
                    tempArrayList.add(dir.listFiles()[0]);

                    System.out.println("Name of the file received from client: " + tempArrayList.get(0).getName());

                    //the zip file to be sent back "borrows" the name of the .lz4 file
                    fileToSendName = fileToReceiveName;

                    //remove lz4 extension
                    if (fileToSendName.contains(".lz4")) {
                        fileToSendName = fileToSendName.substring(0, fileToSendName.length() - 4);
                    }


                    ZipNativeLibrary.archiveFiles(tempArrayList, zipFilesFolderPath, fileToSendName);

                    //create the zip_ROA and send the zip file length
                    File zipFile = new File(zipFilesFolderPath + "/" + fileToSendName + ".zip");
                    long zipFileLength = zipFile.length();
                    System.out.println("ServerSide: The file to send has " + zipFileLength + " bytes");
                    fileToSend_ROA = new RandomAccessFile(zipFile, "r");

                    dout.write(createDataPacket(OPTION_128, String.valueOf(zipFileLength).getBytes("UTF8")));
                    dout.flush();

                    break;

                //read the zip file bytes and send back
                case 129:
                    fileToSendPointerPosition = Long.valueOf(new String(informationBytes));
                    long remainingBytesToRead = fileToSend_ROA.length() - fileToSendPointerPosition;

                    //always reads READ_LIMIT bytes from file unless there are less than READ_LIMIT bytes to read
                    long buff_len = remainingBytesToRead < READ_LIMIT ? remainingBytesToRead : READ_LIMIT;
                    byte[] bytesReadFromFile = new byte[(int) buff_len];

                    if (fileToSendPointerPosition != fileToSend_ROA.length()) {

                        //the server tells the client from which position to read the next bytes
                        fileToSend_ROA.seek(fileToSendPointerPosition);
                        fileToSend_ROA.read(bytesReadFromFile, 0, (int) buff_len);

                        dout.write(createDataPacket(OPTION_130, bytesReadFromFile));
                        dout.flush();

                        float percentageUploaded = ((float) fileToSendPointerPosition / fileToSend_ROA.length()) * 100;

                        if ((int) percentageUploaded > 95) {
                            //the fileToReceiveName was updated to the name of the zip file
                            System.out.println("The server sent to client: " + percentageUploaded + "%" + " of " + fileToSendName);
                        }

                        //the zip was sent successfully, sent close message
                    } else {
                        communicationNotFinished = false;

                        dout.write(createDataPacket(OPTION_131, "Close".getBytes("UTF8")));
                        dout.flush();

                        //deleteContentOfStorageDirectories();
                    }

                    break;

                default:

                    System.out.println("CliendSide: Invalid cmd option received");
            }
        } catch (IOException e) {
            System.err.println("TcpClientWorker: I/O error in communicateWithClient");
            e.printStackTrace();
        }

    }

    /**
     * after the zip file was sent to client, the content of storage folders is deleted in order to keep things simple
     */
    private void deleteContentOfStorageDirectories() {
            deleteContentOfDirectory(lz4ArchivesFolderPath);
            deleteContentOfDirectory(lz4UnarchivedFilesFolderPath);
            deleteContentOfDirectory(zipFilesFolderPath);

    }

    private void deleteContentOfDirectory(String fullPath) {
        File dir = new File(fullPath);
        File[] files = dir.listFiles();

        for (File f : files) {
            f.delete();
        }
    }
}
