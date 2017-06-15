package com.mygdx.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**
 * Created by Marius on 11.06.2017.
 */

public class TcpClient implements Runnable {

    //each  packet contains at maximum READ_LIMIT "useful" bytes of info
    private static final int READ_LIMIT = 7 * 8192; //56 KB


    //communication
    private DataInputStream din;
    private DataOutputStream dout;
    private Socket client;

    //logic
    private String inputFileFullPath;
    private String outputZipFilePath;
    private byte[] OPTION_122;
    private byte[] OPTION_124;
    private byte[] OPTION_126;
    private byte[] OPTION_127;
    private byte[] OPTION_129;
    private boolean communicationNotFinished = true;

    private File fileToSend;
    private RandomAccessFile fileToSend_ROA;
    private long fileToSendPointerPosition;
    private RandomAccessFile fileToReceive_ROA;
    private long fileToReceivePointerPosition;
    private long fileToReceiveLength;
    private String fTR_Name;
    private byte[] separator;


    public TcpClient(String hostName, int portNumber, String inputFileFullPath, String outputZipFilePath) {
        try {
            client = new Socket(hostName, portNumber);
            din = new DataInputStream(client.getInputStream());
            dout = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            System.err.println("Couldn't create socket channel in FileSender");
            e.printStackTrace();
        }

        try {
            OPTION_122 = "122".getBytes("UTF8");
            OPTION_124 = "124".getBytes("UTF8");
            OPTION_126 = "126".getBytes("UTF8");
            OPTION_127 = "127".getBytes("UTF8");
            OPTION_129 = "129".getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("TcpClient: Unknown encoding format ");
            e.printStackTrace();
        }

        separator = new byte[1];
        separator[0] = '\\';

        this.inputFileFullPath = inputFileFullPath;
        this.outputZipFilePath = outputZipFilePath;

        try {
            fileToSend = new File(inputFileFullPath);
            fileToSend_ROA = new RandomAccessFile(fileToSend, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void run() {
        try {
            //first message send to the server is the file's name
            dout.write(this.createDataPacket(OPTION_122, fileToSend.getName().getBytes("UTF8")));
            dout.flush();

                while (communicationNotFinished) {
                    byte[] cmd_buf = new byte[3];
                    din.read(cmd_buf, 0, cmd_buf.length);

                    byte[] informationBytes = readStream();

                    int opt = Integer.parseInt(new String(cmd_buf));

                    communicateWithServerWorker(opt, informationBytes);
                }


            System.out.println("ClientSide: closing the streams of client socket");

            dout.close();
            din.close();
            client.close();

        } catch (UnsupportedEncodingException e) {
            System.err.println("TcpClient: Unknown encoding format ");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O error when trying to create client socket");
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
            int bytesRead = 0;
            int byteOffset = 0;

            while (byteOffset < totalBytesToRead) {
                bytesRead = din.read(data_buff, byteOffset, totalBytesToRead - byteOffset);
                byteOffset += bytesRead;
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

            byte[] lengthOfMessage = String.valueOf(data.length).getBytes("UTF8");

            //the standard form of a packet contains the bytes using the format from below : [command, length of information in bytes, separator, useful information]
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

    private void communicateWithServerWorker(int opt, byte[] informationBytes) {

        try {
            switch (opt) {

                //sends the file length
                case 123:
                        long fileLength = fileToSend_ROA.length();

                        // System.out.println("file length " + fileLength);
                        byte[] fileLengthByteRepresentation = (String.valueOf(fileLength)).getBytes("UTF8");

                        dout.write(createDataPacket(OPTION_124, fileLengthByteRepresentation));
                        dout.flush();

                    break;

                //sends to the server next packet containing file bytes
                case 125:

                        fileToSendPointerPosition = Long.valueOf(new String(informationBytes));
                        long remainingBytesToRead = fileToSend_ROA.length() - fileToSendPointerPosition;

                        //always reads READ_LIMIT bytes from file unless there are less than READ_LIMIT bytes to read
                        long buff_len = remainingBytesToRead < READ_LIMIT ? remainingBytesToRead : READ_LIMIT;
                        byte[] bytesReadFromFile = new byte[(int) buff_len];

                            if (fileToSendPointerPosition != fileToSend_ROA.length()) {

                                //the server tells the client from which position to read the next bytes
                                fileToSend_ROA.seek(fileToSendPointerPosition);
                                fileToSend_ROA.read(bytesReadFromFile, 0, (int) buff_len);

                                dout.write(createDataPacket(OPTION_126, bytesReadFromFile));
                                dout.flush();

                                float percentageUploaded = ((float) fileToSendPointerPosition / fileToSend_ROA.length()) * 100;

                                if (percentageUploaded == 100.0){
                                    System.out.println("The client uploaded:" + percentageUploaded + "%" + " of " + fileToSend.getName());
                                }


                            } else {
                                //ready to receive the zip file
                                createZipFile();
                                dout.write(createDataPacket(OPTION_127, "Ready to receive the zip file".getBytes("UTF8")));
                                dout.flush();
                            }

                    break;

                case 128:
                        fileToReceiveLength = Long.valueOf(new String(informationBytes));
                        //System.out.println("ClientSide: The file to receive has " + fileToReceiveLength + " bytes");

                        //asks for the next data packet providing the position where it will write, initially 0
                        dout.write(createDataPacket(OPTION_129, String.valueOf(fileToReceivePointerPosition).getBytes("UTF8")));
                        dout.flush();

                    break;

                //receive the zip bytes
                case 130:
                        //update the writing position: position += (old)currentFilePointer; initially will set the pos to 0 (nothing was written before)
                        fileToReceive_ROA.seek(fileToReceivePointerPosition);
                        fileToReceive_ROA.write(informationBytes);

                        //a temp variable
                        fileToReceivePointerPosition = fileToReceive_ROA.getFilePointer();
                        float percentage  =  ((float) fileToReceivePointerPosition / fileToReceiveLength) * 100;

                        if (  percentage == 100.0){
                            // fTR_Name contains the full file path, thus I will trim it to display only the fileName
                            int length = fTR_Name.length();
                            int beginIndex = fTR_Name.lastIndexOf('/');
                            String fileReceivedName = fTR_Name.substring(beginIndex + 1, length);

                            System.out.println("Client Downloaded:" + percentage + "%" + " of " + fileReceivedName) ;
                        }


                        //asks for the next data packet providing the position from where to read
                        dout.write(createDataPacket(OPTION_129, String.valueOf(fileToReceivePointerPosition).getBytes("UTF8")));
                        dout.flush();

                    break;

                case 131:

                        //the last message received from server when everything was sent

                            System.out.println("The zip file " + fTR_Name + " was downloaded successfully on client side!");
                            communicationNotFinished = false;


                    break;

                default:

                    System.out.println("CliendSide: Invalid cmd option received");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createZipFile() {
        try {
            String fileToSendName = fileToSend.getName();

            if (fileToSendName.contains(".")){
                int length = fileToSendName.length();
                fileToSendName = fileToSendName.substring(0, length - 4);
            }

            fTR_Name = outputZipFilePath + "/" + fileToSendName + ".zip";
            fileToReceive_ROA = new RandomAccessFile(fTR_Name, "rw");
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't create the file to receive on ClientSide");
            e.printStackTrace();
        }
    }
}

