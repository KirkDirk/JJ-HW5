package ru.kirkdirk;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("-----\nСтарт клиента ...");

        Scanner scanner = new Scanner(System.in);
        // Получаем имя подключаемого клиента
        System.out.println("---\nВведите свое имя:");
        String name = scanner.nextLine();

        try {
            // Устанавливаем соединение с подключаемым клиентом
            Socket socket = new Socket("localhost", 2222);
            // Инициализируем класс Сlient для работы с подключенным клиентом
            Client client = new Client(socket, name);
            // Получаем информацию о подключаемом клиенте
            InetAddress inetAddress = socket.getInetAddress();
            System.out.println(" InetAddress: " + inetAddress);
            String remoteIP = inetAddress.getHostAddress();
            System.out.println(" Remote IP: " + remoteIP);
            System.out.println(" LocalPort: " + socket.getLocalPort());

            // Комментарий для отправки личных сообщений
            System.out.println("---\nДля отправки личного сообщения используйте формат '@имя сообщение'");

            // Инициализируем поток слушателя входящих сообщений
            client.listenForMessage();
            // Инициализируем поток для отправки сообщений
            client.sendMessage();

        } catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}