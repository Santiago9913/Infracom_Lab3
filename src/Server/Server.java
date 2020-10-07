package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int nThreads = 25;
    public final static String A7X = "files/a7x.mp4";
    public final static String ROSES = "files/roses.pdf";

    private static ServerSocket ss;
    private static int numClientes;
    public static String file_name="";
    private static HashMap<Integer, ServerThread> clientes = new HashMap<>();

    public static synchronized void removeClient(int id){
        clientes.remove(id);
    }



    public static void sendAll(File file) throws Exception{
        for(Integer key : clientes.keySet()){
            ServerThread actual = clientes.get(key);
            actual.sendFile(file);
        }
    }


    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese El HOST: ");
        String host = sc.next();

        System.out.println("Ingrese El PORT: ");
        int port = sc.nextInt();



        ss = new ServerSocket(port);
        System.out.println("[!] Iniciando Servidor En " + host +":" +port);


        for(int i = 1; true; i++){
            try{
                Socket socketC = ss.accept();
                ServerThread cliente = new ServerThread(socketC, i);
                cliente.start();
//                cliente.run();
                clientes.put(i,cliente);
                numClientes = clientes.size();
                Thread.sleep(1000);

                System.out.println("El numero actual de clientes es: "+numClientes);
                System.out.println("Desea enviar un archivo? (SI o NO)");
                String resp = sc.next();
                if(resp.toUpperCase().equals("SI")){
                    System.out.println("Seleccione el archivo (1 o 2)");
                    System.out.println("    1 - A7X.mp4");
                    System.out.println("    2 - ROSES.pdf");
                    int file_num = sc.nextInt();
                    file_name = file_num == 1 ? A7X : ROSES;
                    File file = new File(file_name);
                    //sendAll(file.getCanonicalPath());
                    sAll(pool);
                } else {
                    System.out.println("Esperando a mas clientes...");
                    System.out.println("El numero actual de clientes es: "+numClientes);
                }


            } catch (IOException e) {
                System.out.println("Error creando el socket cliente.");
                e.printStackTrace();
            }
        }
    }
}
