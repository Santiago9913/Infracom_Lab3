package Client;

import Server.Server;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class Client {

    private static final String HOLA = "HOLA";
    private static final String OK = "OK";
    private static final String PADDING_AES = "AES/ECB/PKCS5Padding";
    private static final String MSJ = "MSJ";


    private int puerto;

    private String host;


    private Socket socket;


    private PrintWriter pw;


    private BufferedReader brServer;


    private int bufferSize;

    String postFix;

    String serverSum = "";

    public Client(int puerto, String host) throws IOException {
        this.puerto = puerto;
        this.host = host;
        this.socket = new Socket(host, puerto);
        this.brServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.pw = new PrintWriter(this.socket.getOutputStream(), true);
        this.bufferSize = 4096;
    }



    public static String toHexString(byte[] array) {
        return DatatypeConverter.printBase64Binary(array);
    }

    public static byte[] toByteArray(String s) {
        return DatatypeConverter.parseBase64Binary(s);
    }

    private boolean checkSuma(int size, File file, String serverSum) throws IOException{
        FileInputStream in = new FileInputStream(file);
        CheckedInputStream ck = new CheckedInputStream(in,new CRC32());
        byte[] buffer = new byte[size];
        while(ck.read(buffer,0,buffer.length)>=0){}
        long sum = ck.getChecksum().getValue();
        byte[] sumBy = ByteBuffer.allocate(8).putLong(sum).array();
        String sumStr = toHexString(sumBy);
        System.out.println("Clientes: " + sumStr);
        System.out.println("Servidor: " + serverSum);
        in.close();
        return serverSum.equals(sumStr)  ;
    }

    public void recieveFile() throws IOException {
        File file = new File("./copias/copia2."+postFix);
        byte[] buffer =  new byte[2*bufferSize];
        InputStream in = socket.getInputStream();
        OutputStream out = new FileOutputStream(file.getCanonicalPath());
        int count;
        while((count = in.read(buffer)) > 0){
            out.write(buffer,0,count);
        }
        in.close();
        out.close();
        System.out.println(checkSuma(2*bufferSize,file,serverSum));
        socket.close();

    }

    public void run() throws Exception{
        System.out.println("Estableciendo Conexion Con El Servidor...");
        while(true){
            pw.println(HOLA);
            String msjServidor = brServer.readLine();

            if(msjServidor.equals(OK)){
                pw.println(OK);
            }

            msjServidor = brServer.readLine();
            postFix = msjServidor;
            msjServidor = brServer.readLine();
            if(msjServidor.equals(OK)){
                serverSum = brServer.readLine();
                recieveFile();
            }
            socket.close();
            break;
        }
        System.out.println("Archivo Recibido");
    }

}
