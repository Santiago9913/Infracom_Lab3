package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int nThreads = 25;

    private static ServerSocket ss;

        public static void main(String[] args) throws Exception {
            Scanner sc = new Scanner(System.in);
            System.out.println("Ingrese El HOST: ");
            String host = sc.next();

            System.out.println("Ingrese El PORT: ");
            int port = sc.nextInt();

            ss = new ServerSocket(port);
            System.out.println("[!] Iniciando Servidor En " + host +":" +port);

            ExecutorService pool = Executors.newFixedThreadPool(nThreads);

            for(int i = 0; true; i++){
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
