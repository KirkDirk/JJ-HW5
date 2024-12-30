package ru.kirkdirk;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable{

    private final Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String name;

    /**
     * Список подключенных клиентов
     */
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

        // Переменные для определения адресного клиента
        String nameTo;
        String messageTo;

        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                // Ведем типа лог сообщений на сервере
                System.out.println(messageFromClient);
                // Проверяем, не является ли сообщение адресным
                // Сначала откидываем вступление сообщения "имя_отправителя:"
                int index = messageFromClient.indexOf(" ");
                messageTo = messageFromClient.substring(index+1);
                // Ищем собаку в начале сообщения и вызываем отправку сообщения адресному клиенту
                String first = String.valueOf(messageTo.charAt(0));
                if (first.equals("@")) {
                    // определяем имя клиента
                    index = messageTo.indexOf(" ");
                    nameTo = messageTo.substring(1,index);
                    // отрезаем собственно сообщение
                    messageTo = messageTo.substring(index+1);
                    // отправляем сообщение клиенту
                    sendMessageTo(nameTo, messageTo);
                } else {
                    // Отправляем сообщение всем клиентам, если сообщение неадресное
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void sendMessageTo(String nameClient, String message){
        for (ClientManager client:clients){
            if (client.name.equals(nameClient)){
                try {
                    client.bufferedWriter.write(name + " (личное): " + message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                } catch (IOException e){
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
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
