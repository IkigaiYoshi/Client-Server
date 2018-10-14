//package org.suai.cs_1;


import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientUDP {
    private DatagramSocket clientUDP;
    private InetAddress ipAddress;
    private byte[] data;
    private DataOutputStream out;
    private Socket clientTCP;
    private String filePath = "/home/ikigai/IdeaProjects/cs/src/org/suai/Client/";


    public ClientUDP(String port, String ip) {
        try {
            //подключение UDP сервера
            System.out.println("Welcome to Client side");
            data = new byte[1024];

            Scanner scanner = new Scanner(System.in);
            clientUDP = new DatagramSocket();
            ipAddress = InetAddress.getByName(ip);

            DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, Integer.parseInt(port));
            clientUDP.send(sendPacket);
            Thread.sleep(10);

            //подключение TCP для передачи файлов
            clientTCP = new Socket(ip, 5643);
            out = new DataOutputStream(clientTCP.getOutputStream());

            ThreadUDP in = new ThreadUDP(clientUDP, clientTCP, filePath);
            in.start();

            while (true) {
                String msg = scanner.nextLine();
                if (msg.contains("@sendfile")) {
                    //передача файла
                    String[] dataString = msg.split(" ");
                    if (dataString.length <= 1) continue;
                    data = dataString[0].getBytes();
                    sendPacket = new DatagramPacket(data, dataString[0].length(), ipAddress, Integer.parseInt(port));
                    clientUDP.send(sendPacket);
                    sendFile(dataString[1]);

                } else {
                    data = msg.getBytes();
                    sendPacket = new DatagramPacket(data, msg.length(), ipAddress, Integer.parseInt(port));
                    clientUDP.send(sendPacket);
                }

                if (msg.contains("@quit")) {
                    System.exit(-1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }


    private void close() {
        try {
            clientUDP.close();
            clientTCP.close();
        } catch (Exception e) {
            System.out.println("Doh', error");
        }
    }


    private void sendFile(String fileName) {
        try {
            File file = new File(filePath + fileName);
            out.writeLong(file.length());
            out.writeUTF(file.getName());
            FileInputStream inputFile = new FileInputStream(file);
            byte[] buffer = new byte[64 * 1024];
            int count;
            while ((count = inputFile.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            out.flush();
            inputFile.close();
        } catch (IOException ex) {
        }
    }


}
