package ru.kirkdirk;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Socket socket;
    private final String name;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public Client(Socket socket, String userName) {
        this.socket = socket;
        name = userName;
        try {
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        };
    }

    /**
     * Метод для слушания входящих сообщений
     */
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()){
                    try {
                        message = bufferedReader.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }

    /**
     * Метод для отправки сообщений
     */
    public void sendMessage(){
        try{
            bufferedWriter.write(name);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String message = scanner.nextLine();
                bufferedWriter.write(name + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader,bufferedWriter);            ;
        }
    }

    /**
     * Метод аварийного отключения клиента, если в процессе работы с ним
     * произошли несуразицы
     * @param socket
     * @param br
     * @param bw
     */
    private void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw){
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
}
