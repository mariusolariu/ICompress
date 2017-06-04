package com.mygdx.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Ronan
 * on 01/05/2017.
 */
public class ClientProcessor implements Runnable{
    private Socket sock;
    private ObjectOutputStream writer = null;
    private ObjectInputStream reader = null;

    public ClientProcessor(Socket pSock){
        sock = pSock;
    }

    //We treat the requests in another thread
    public void run(){
        System.out.println("Launching client connection processing");
        boolean closeConnexion = false;

        //As long as the connection is active, we treat the requests
        while(!sock.isClosed()){
            try {

                writer = new ObjectOutputStream(sock.getOutputStream());
                reader = new ObjectInputStream(sock.getInputStream());

                //We are waiting the client's request
                //TODO change type
                Object response = read();
                InetSocketAddress remote = (InetSocketAddress)sock.getRemoteSocketAddress();

                //We display some information for the debug
                String debug;
                debug = "Thread : " + Thread.currentThread().getName() + ". ";
                debug += "Request the address : " + remote.getAddress().getHostAddress() +".";
                debug += " On the port : " + remote.getPort() + ". ";
                debug += "\t -> Order received : " + response.toString() + "\n";

                System.out.println(debug);

                //We treat the client's request in function of the sent request
                Object toSend = null;


                //TODO create the result


                //We send the reply to the client
                writer.writeObject(toSend);

                System.out.println("Object sent to the client.");

                //WE MUST USE flush()
                //Otherwise the data will not be transmitted
                //to the client and it will wait indefinitely
                writer.flush();

            }catch(SocketException e){
                System.err.println("THE CONNECTION IS INTERRUPTED ! ");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Method to read the client's reply
    private Object read() throws IOException {
        Object result = null;
        try {
            result = reader.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
