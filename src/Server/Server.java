package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int nThreads = 25;
    public final static String A7X = "../files/a7x.mp4";
    public final static String ROSES = "../files/roses.pdf";

    private static ServerSocket ss;
    private static int clientes;
    public static String file_name;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese El HOST: ");
        String host = sc.next();

        System.out.println("Ingrese El PORT: ");
        int port = sc.nextInt();

        System.out.println("Seleccione el archivo (1 o 2)");
        System.out.println("    1 - A7X.mp4");
        System.out.println("    2 - ROSES.pdf");
        int file_num = sc.nextInt();
        while (file_num != 1 && file_num != 2) {
            file_name = file_num == 1 ? A7X : ROSES;
        }

        ss = new ServerSocket(port);
        System.out.println("[!] Iniciando Servidor En " + host +":" +port);

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        for(int i = 1; true; i++){
            try{
                Socket socketC = ss.accept();
                pool.execute(new ServerThread(socketC, i));

            } catch (IOException e) {
                System.out.println("Error creando el socket cliente.");
                e.printStackTrace();
            }
        }
    }
}
