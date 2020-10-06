package Server;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {

    private static final String HOLA = "HOLA";
    private static final String OK = "OK";
    private static final String ERROR = "ERROR";

    private int id;
    private Socket socket;
    
    public ServerThread(Socket socket, int id){
        this.id = id;
        this.socket = socket;
    }


    public void run(){
        System.out.println("Atendiendo Cliente: " + id);
        String linea = "";
        String file = Server.file_name;

        try{
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            linea = bf.readLine();
            if(linea.equals(HOLA)){
                pw.println(OK);
            } else {
                pw.println(ERROR);
                socket.close();
                throw new Exception("Error al conectarse");
            }

            linea = bf.readLine();
            if(linea.equals(OK)){
                System.out.println("El cliente: "+id+" esta listo");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
