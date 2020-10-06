package Server;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {

    private static final String HOLA = "HOLA";
    private static final String OK = "OK";
    private static final String ERROR = "ERROR";
    private static final String MSJ = "MSJ";

    private int id;
    private Socket socket;

    private PrintWriter pw;
    private BufferedReader bf;
    
    public ServerThread(Socket socket, int id){
        this.id = id;
        this.socket = socket;
    }

    public Socket getSocket(){
        return this.socket;
    }

    public int getIdClient(){
        return this.id;
    }

    public synchronized void sendFile(File file) {
        try {
            byte[] buffer = new byte[4096];
            InputStream in = new FileInputStream(file);
            OutputStream out = socket.getOutputStream();

            int count;
            while ((count = in.read(buffer))>0){
                out.write(buffer,0,count);
            }
            in.close();
            out.close();

            pw.println(MSJ);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void wakeUp(){
        this.notify();
    }

    public void run(){
        System.out.println("Atendiendo Cliente: " + id);
        String linea = "";

        try{
            pw = new PrintWriter(socket.getOutputStream(), true);
            bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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

            this.wait();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
