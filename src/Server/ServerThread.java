package Server;

import java.io.*;
import java.net.Socket;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

public class ServerThread extends Thread {

    private static final String HOLA = "HOLA";
    private static final String OK = "OK";
    private static final String ERROR = "ERROR";
    private static final String PADDING_AES = "AES/ECB/PKCS5Padding";

    private static X509Certificate certificado;
    private static KeyPair keyPair;
    private int id;
    private Socket socket;
    private byte[] mybyte;


    public static void init(X509Certificate cert, KeyPair pKeyPair){
        certificado = cert;
        keyPair = pKeyPair;
    }

    public ServerThread(Socket socket, int id){
        this.id = id;
        this.socket = socket;

        try {
            mybyte = new byte[520];
            mybyte = certificado.getEncoded();
        } catch (Exception e) {
            System.out.println("Error creando el thread");
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
                System.out.println("Conexion Exitosa Con Cliente...");
            } else{
                pw.println(ERROR);
                socket.close();
                throw  new Exception("Error al conectarse");
            }

        } catch (Exception e){
            e.printStackTrace();
        }



    }
}
