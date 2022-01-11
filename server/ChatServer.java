package server;

import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.PrintWriter;






public class ChatServer {
    public static final int PORT = 1112;

    public ChatServer(){
        ServerSocket serverSock = null;
        List<PrintWriter> listWriter = new ArrayList<PrintWriter>();
    
        try{
            // creating new socket,binding socket with host address and port
            serverSock = new ServerSocket();

            String hostaddr = InetAddress.getLocalHost().getHostAddress();
            serverSock.bind(new InetSocketAddress(hostaddr,PORT));
            chatlog.out("server address : " + hostaddr + ", port : " + PORT);
            
            // accepting tcp request from clients
            while(true){
                Socket socket = serverSock.accept();
                new ChatServerThread( listWriter, socket).start();
            }
        }
        catch (Exception e) {
        e.printStackTrace();
        }
        finally{
            // Server socket clean up, closing socket if not null and open
            try{

                if (serverSock != null && !serverSock.isClosed()){
                    serverSock.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                }
        }
    
    
    
    
    }
}
