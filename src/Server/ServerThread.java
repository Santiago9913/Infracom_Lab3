package Server;

import Client.Client;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

public class ServerThread extends Thread {

    private static final String HOLA = "HOLA";
    private static final String OK = "OK";
    private static final String ERROR = "ERROR";
    private static final String MSJ = "MSJ";

    private int id;
    private Socket socket;

    private PrintWriter pw;
    private BufferedReader bf;

    int bufferSize = 4096;

    
    public ServerThread(Socket socket, int id){
        this.id = id;
        this.socket = socket;
    }

    public static String toHexString(byte[] array) {
        return DatatypeConverter.printBase64Binary(array);
    }

    public Socket getSocket(){
        return this.socket;
    }

    public int getIdClient(){
        return this.id;
    }

    private long getCRC32Checksum(File file, int size) throws IOException{
        FileInputStream in = new FileInputStream(file);
        CheckedInputStream ck = new CheckedInputStream(in,new CRC32());
        byte[] buffer = new byte[size];
        while(ck.read(buffer,0,buffer.length)>=0){}
        in.close();
        return ck.getChecksum().getValue();
    }

    public synchronized void sendFile(File file) {
        try {
            long sum = getCRC32Checksum(file,2*bufferSize);
            byte[] sumBy = ByteBuffer.allocate(8).putLong(sum).array();
            String sumStr = toHexString(sumBy);
            System.out.println("ServerThread: " + sumStr);


            byte[] buffer = new byte[2*bufferSize];
            InputStream in = new FileInputStream(file);
            OutputStream out = socket.getOutputStream();
            int n = file.getPath().length();

            pw.println(file.getPath().substring(n-3,n));
            pw.println(OK);
            pw.println(sumStr);


            long inicio = System.currentTimeMillis();
            int count;
            while ((count = in.read(buffer))>0){
                out.write(buffer,0,count);
            }
            long fin = System.currentTimeMillis() - inicio;
            System.out.println(fin);
            in.close();
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
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

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
