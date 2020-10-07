package Client;

import java.util.Scanner;

public class Main {

    public static void run() throws Exception{
        System.out.println("Ingrese El Host Al Que Se Desea Conectar: ");
        String host = "0.0.0.0";
        System.out.println("Ingrese El Port Al Que Se Desea Conectar: ");
        int port = 8080;
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
