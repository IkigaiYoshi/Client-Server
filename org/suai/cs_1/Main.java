//package org.suai.cs_1;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Работать как Клиент или Сервер? (S(erver) / C(lient))");
        while (true) {
            char answer = Character.toLowerCase(in.nextLine().charAt(0));
            if (answer == 's') {
                new ServerUDP(args[0]);
                break;
            } else if (answer == 'c') {
                new ClientUDP(args[0], args[1]);
                break;
            } else {
                System.out.println("Doh', error");
            }
        }
    }
}
