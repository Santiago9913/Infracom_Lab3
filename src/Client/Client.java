package Client;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;


public class Client {

    private static final String HOLA = "HOLA";
    private static final String OK = "OK";
    private static final String PADDING_AES = "AES/ECB/PKCS5Padding";


    private int puerto;

    private String host;


    private Socket socket;


    private PrintWriter pw;


    private BufferedReader brServer;


    private BufferedReader brCliente;

    private int id;



    public Client(int puerto, String host) throws IOException {
        this.puerto = puerto;
        this.host = host;
        this.socket = new Socket(host, puerto);
        this.brCliente = new BufferedReader(new InputStreamReader(System.in));
        this.brServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.pw = new PrintWriter(this.socket.getOutputStream(), true);
    }

    public int getIdCliente(){
        return this.id;
    }

    public static String toHexString(byte[] array) {
        return DatatypeConverter.printBase64Binary(array);
    }

    public static byte[] toByteArray(String s) {
        return DatatypeConverter.parseBase64Binary(s);
    }



    public void run() throws Exception{
        while(true){
            System.out.println("Estableciendo Conexion Con El Servidor...");
            pw.println(HOLA);
            String msjServidor = brServer.readLine();

            if(msjServidor.equals(OK)){
                pw.println(OK);
            }
        }
    }

}
