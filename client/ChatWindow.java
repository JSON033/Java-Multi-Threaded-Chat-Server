package client;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

//import javafx.scene.paint.Color;

//import javafx.scene.control.TextArea;
//import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow {
    private String alias;
    private Frame frame;
    private Button button;
    private Panel panel;
    private TextField field;
    private TextArea area;
    private Socket sock;

    public ChatWindow(String alias, Socket sock){
        this.alias = alias;
        this.sock = sock;

        frame = new Frame(alias);
        panel = new Panel();
        button = new Button("Send");
        field = new TextField();
        area = new TextArea(40,80);

        new ClientReceiveThread(sock).start();
    }

    public void show(){
        // setting up chat window with button, panel, textarea, and textfield inside frame instance
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendmessage();
            }
        });

        field.setColumns(80);
        field.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent e ){
                char code = e.getKeyChar();
                if (code == KeyEvent.VK_ENTER){
                    sendmessage();
                }
            }
        });
        panel.setBackground(Color.LIGHT_GRAY);
        panel.add(field);
        panel.add(button);
        frame.add(BorderLayout.SOUTH, panel);


        area.setEditable(false);
        frame.add(BorderLayout.CENTER,area);

        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                PrintWriter writer;
                try{
                    writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),StandardCharsets.UTF_8),true);
                    String request = "quit\r\n";
                    writer.println(request);
                    System.exit(0);
                }
                catch(Exception ee){
                    ee.printStackTrace();
                }
            }
        });
        frame.setVisible(true);
        frame.pack();


    }
    
    public void sendmessage(){
        PrintWriter writer;
        try{
            // sending message to server 
            // Printwriter object for socket output stream takes in string var from textfield 
            // formats a message request with textfield payload with message keyphrase
            // afterwards reset textfield to empty and focused
            writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_8), true);
            String message = field.getText();
            String request = "message: " + message + "\r\n";
            writer.println(request);

            field.setText("");
            field.requestFocus();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private class ClientReceiveThread extends Thread{
        Socket sock = null;
    
        public ClientReceiveThread(Socket sock){
            this.sock = sock;
        }

        public void run(){
            try {
                // threadsafe reader that takes sockets input stream
                BufferedReader buffreader = new BufferedReader(new InputStreamReader(sock.getInputStream(),StandardCharsets.UTF_8)); 
                 
                    while(true){
                        String msg = buffreader.readLine();
                        chatlog.out(msg);


                        area.append(msg);
                        area.append("\n");

                    }
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }
    }
}
