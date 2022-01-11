package server;

import java.net.Socket;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import server.chatlog;

public class ChatServerThread extends Thread{
    private String alias = null;
    private Socket sock = null;
    List<PrintWriter> listwriter = null;

    public ChatServerThread(List<PrintWriter> listwriter, Socket sock){
        this.sock = sock;
        this.listwriter = listwriter;
    }
    public void run() {
        try{
        //Creating a reader and writer for socket's input and output streams
        BufferedReader buffreader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_8));


            while(true){
                // rendering request from buffer, 3 different paths QUIT,JOIN,MESSAGE
                String request = buffreader.readLine();
                
                if(request == null){
                    quit(printwriter);
                    chatlog.out("Client disconnected");
                    break;
                }
                chatlog.out(request);
                String[] token = request.split(":");
                if("join".equals(token[0])){
                    String joinedstring = String.join("", Arrays.copyOfRange(token, 1, token.length));
                    join(joinedstring,printwriter);
                }
                else if ("message".equals(token[0])){
                    String joinedstring = String.join("", Arrays.copyOfRange(token, 1, token.length));
                    message(joinedstring);
                }
                else if("quit".equals(token[0])){
                    quit(printwriter);
                }
            }
        }
        
            catch(Exception e){
                chatlog.out(this.alias + "quit");
            }
        }
        // QUIT: remove writer from list and broadcast client quit
        private void quit(PrintWriter writer){
            removeWriter(writer);
            String msg = this.alias + "quit";
            broadcast(msg);
        }

        private void removeWriter(PrintWriter writer){
            synchronized(listwriter){
                listwriter.remove(writer);
            }
        }
        // JOIN: Broadcast user joined and add user to list of Printwriter
        private void join(String alias, PrintWriter writer){
            this.alias = alias;
            String msg = alias +"joined";
            broadcast(msg);
            addwriter(writer);
        }
        private void message(String msg){
            broadcast(this.alias +":" +msg);
        }
        // addwriter: mutex lock on list to add writer
        private void addwriter(PrintWriter writer){
            synchronized(listwriter){
                listwriter.add(writer);
            }
        }
        // broadcast: thread takes control of list and updates 
        private void broadcast(String msg){
            synchronized(listwriter){
                for(PrintWriter writer : listwriter){
                    writer.println(msg);
                    writer.flush();
                }
            }
        }

}
