package ru.kirkdirk;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;


    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void runServer(){
        try {
            while ((!serverSocket.isClosed())) {
                Socket socket = serverSocket.accept();
                ClientManager clientManager = new ClientManager(socket);
                System.out.println("---\nПодключен новый клиент!");
                // Ожидаем подключения в отдельном потоке, чтобы избежать блокировки основного
                Thread thread = new Thread(clientManager);
                thread.start();
            }
        } catch (IOException e){
            closeSocket();
        }
    }

    private void closeSocket(){
        try {
            if (serverSocket!= null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
