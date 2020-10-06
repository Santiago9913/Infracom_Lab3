package Client;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;
import org.apache.commons.io.IOUtils;

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


    public static <T extends OutputStream> T copy(InputStream in, T out)
            throws IOException {
        byte[] buffer = new byte[1024];
        for (int r = in.read(buffer); r != -1; r = in.read(buffer)) {
            out.write(buffer, 0, r);
        }
        return out;
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
        System.out.println("Estableciendo Conexion Con El Servidor...");
        while(true){
            pw.println(HOLA);
            String msjServidor = brServer.readLine();

            if(msjServidor.equals(OK)){
                pw.println(OK);
            }

            byte[] buffer = new byte[4096];
            InputStream in = socket.getInputStream();
            OutputStream out = new FileOutputStream("./copia.pdf");
            byte[] content = copy(in, new ByteArrayOutputStream()).toByteArray();
            copy(new ByteArrayInputStream(content), out);
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            File file = new File("./recivido.pdf");
//            FileWriter fw = new FileWriter(file);
//            FileOutputStream fw = new FileOutputStream(new BufferedInputStream())
//            int count;
//
//            while(in.read() > 0){
//
//            }

        }
    }

}
