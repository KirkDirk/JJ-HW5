package ru.kirkdirk;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {

        System.out.println("-----\nСтарт сервера...");

        try {
            ServerSocket serverSocket = new ServerSocket(2222);
            Server server = new Server(serverSocket);
            server.runServer();

        } catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}