package Client;

import java.util.Scanner;

public class Main {

    public static void run() throws Exception{
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese El Host Al Que Se Desea Conectar: ");
        String host = sc.next();
        System.out.println("Ingrese El Port Al Que Se Desea Conectar: ");
        int port = sc.nextInt();
        Client client = new Client(port,host);
        client.run();
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
