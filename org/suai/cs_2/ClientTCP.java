package org.suai.cs_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class ClientTCP {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;


    public ClientTCP(String ip, String port) {
        Scanner scan = new Scanner(System.in);

        try {

            socket = new Socket(ip, Integer.parseInt(port));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //чтение из сокета
            out = new PrintWriter(socket.getOutputStream(), true); //запись

            System.out.println("Имя в чате");
            out.println(scan.nextLine());

            //параллельное принятие сообщений
            Stream stream = new Stream();
            stream.start();

            //отправлене сообщений
            String str = "";
            while (true) {
                str = scan.nextLine();
                out.println(str);
                if (str.contains("@exit")) break;
            }
            stream.setStopConnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }


    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Doh', error");
        }
    }


    private class Stream extends Thread {

        private boolean stopConnect;


        void setStopConnect() {
            stopConnect = true;
        }


        @Override
        public void run() {
            try {
                //пока пользователь не отключился выводим все принятые сообщения
                while (!stopConnect) {
                    String str = in.readLine();
                    if (str != null)
                        System.out.println(str);
                }
            } catch (IOException e) {
                System.out.println("Doh', error");
            }
        }
    }

}