package client;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient {
    private static final String serverip = "142.31.1.59";
    private static final int serverport = 1112;


    public ChatClient(){
        Scanner scan = new Scanner(System.in);
        String name = null;

        while(true){
            System.out.println("Enter name : ");
            name = scan.nextLine();
            
            if(!name.isEmpty()){
                break;
            }
            }
        scan.close();
        Socket sock = new Socket();

        try{
            sock.connect(new InetSocketAddress(serverip, serverport));

            chatlog.out("client joined");

            new ChatWindow(name, sock).show();

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_8),true);

            String request = "join:" + name + "\r\n" ;

            writer.println(request);
            }
            catch (Exception e){

            }


        }
        
    }

