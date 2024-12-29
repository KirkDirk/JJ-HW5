package ru.kirkdirk;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable{

    private final Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String name;

    public final static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println("---\n" + name + " подключился к чату!");
            broadcastMessage("---\nServer: " + name + " подключился к чату!");
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        };

    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void broadcastMessage(String message){
        for (ClientManager client:clients){
            if (!client.name.equals(name)){
                try {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                } catch (IOException e){
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }


    private void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw){
        removeClient();
        try {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient(){
        clients.remove(this);
        System.out.println("---\n" + name + " покинул чат.");
        broadcastMessage("---\nServer: " + name + " покинул чат.");
    }
}
