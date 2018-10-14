//package org.suai.cs_1;


import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;


public class ThreadUDP extends Thread {
    private DatagramSocket socket;
    private DatagramPacket packet;
    private DataInputStream input;
    private String Name;
    private String filePath;


    ThreadUDP(DatagramSocket socket, Socket socketTCP, String filePath) {
        try {
            this.socket = socket;
            Name = "user";
            byte[] receiveData = new byte[1024];
            packet = new DatagramPacket(receiveData, receiveData.length);
            input = new DataInputStream(socketTCP.getInputStream());
            this.filePath = filePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                byte[] receiveData = new byte[1024];
                packet = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(packet);
                String newMassege = new String(packet.getData(), 0, packet.getLength());

                if (newMassege.contains("@name")) {
                    String[] tmp = newMassege.split(" ");
                    StringBuilder temp = new StringBuilder();
                    int i = 0;

                    if (tmp.length != 1) {
                        while (tmp[1].charAt(i) >= 'a' && tmp[1].charAt(i) <= 'z') {
                            temp.append(tmp[1].charAt(i));
                            i++;
                            if (i >= tmp[1].length())
                                break;
                        }
                        Name = temp.toString();
                    }
                } else if (newMassege.contains("@quit")) {
                    System.out.println("Client disconected");
                } else if (newMassege.contains("@sendfile")) {
                    receiveFile();
                } else {
                    System.out.println(Name + ": " + newMassege);
                }
            } catch (IOException e) {

            }
        }
    }

    private void receiveFile() {
        try {
            long fileSize = input.readLong();
            String fileName = input.readUTF();
            byte[] buffer = new byte[64 * 1024];
            FileOutputStream outputFile = new FileOutputStream(filePath + fileName);
            int count, total = 0;
            while ((count = input.read(buffer)) != -1) {
                total += count;
                outputFile.write(buffer, 0, count);
                if (total == fileSize) {
                    break;
                }
            }
            outputFile.flush();
            outputFile.close();
        } catch (IOException ex) {
        }
    }
}