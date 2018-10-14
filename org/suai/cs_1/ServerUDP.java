//package org.suai.cs_1;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class ServerUDP {

    private DatagramSocket serverUDP;
    private InetAddress ipAddress;
    private byte[] data;
    private Socket client;
    private DataOutputStream out;
    private ServerSocket serverTCP;
    private String filePath = "/home/ikigai/IdeaProjects/cs/src/org/suai/Server/";


    public ServerUDP(String port) {
        try {
            serverUDP = new DatagramSocket(Integer.parseInt(port));
            Scanner scanner = new Scanner(System.in);
            String msg;
            System.out.println("Welcome to Server side");

            data = new byte[1024];
            DatagramPacket packet;

            packet = new DatagramPacket(data, data.length);
            serverUDP.receive(packet);


            serverTCP = new ServerSocket(5643);
            client = serverTCP.accept();

            out = new DataOutputStream(client.getOutputStream());


            ipAddress = packet.getAddress();
            int portInet = packet.getPort();

            System.out.println("client is connected");

            ThreadUDP in = new ThreadUDP(serverUDP, client, filePath);
            in.start();

            while (true) {
                msg = scanner.nextLine();
                if (msg.contains("@sendfile")) {
                    String[] dataString = msg.split(" ");
                    if (dataString.length <= 1) continue;
                    data = dataString[0].getBytes();
                    packet = new DatagramPacket(data, dataString[0].length(), ipAddress, portInet);
                    serverUDP.send(packet);
                    sendFile(dataString[1]);

                } else {
                    data = msg.getBytes();
                    packet = new DatagramPacket(data, msg.length(), ipAddress, portInet);
                    serverUDP.send(packet);
                }

                if (msg.contains("@quit")) {
                    System.exit(-1);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            serverUDP.close();
            serverTCP.close();
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
