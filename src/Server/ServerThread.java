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

    public synchronized void sendFile() {
        try {
            this.wait();
            //File transfer
            OutputStream out = socket.getOutputStream();
            File file = new File(Server.file_name);
            InputStream in = new FileInputStream(file.getCanonicalPath());
            byte[] buffer = new byte[4096];
            int count;
            while((count = in.read()) > 0){
                out.write(buffer, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(){
        System.out.println("Atendiendo Cliente: " + id);
        String linea = "";

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
            sendFile();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
