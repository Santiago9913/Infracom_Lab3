package Client;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;

public class Client {

    private static final String HOLA = "HOLA";
    private static final String OK = "OK";
    private static final String PADDING_AES = "AES/ECB/PKCS5Padding";

    /**
     * El puerto en el cual escucha el servidor
     */
    private int puerto;

    private String host;

    /**
     * Con este socket conectamos al servidor usando: socket = new Socket(host, portNumber)
     */
    private Socket socket;

    /**
     * Para escribir datos primitivos en texto
     */
    private PrintWriter pw;

    /**
     * Para leer lo que se escribe por parte del servidor
     */
    private BufferedReader brServer;

    /**
     * Para leer lo que escribe el cliente
     */
    private BufferedReader brCliente;

    private String[] algoritmos;

    private String simetrico;

    private String asimetrico;

    private String hash;

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private PublicKey publicKeyServidor;

    private KeyPair keyPairCliente;

    private SecretKey llaveCompartida;

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

    private KeyPair generarLlaves() throws Exception{
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        keygen.initialize(1024);
        KeyPair keyPair = keygen.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        return keyPair;
    }

    public static X509Certificate generarCertificado(KeyPair keyPair) throws Exception{

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.YEAR, 10);
        X509v3CertificateBuilder x509v3CertificateBuilder = new X509v3CertificateBuilder(
                new X500Name("CN=cliente"),
                BigInteger.valueOf(1),
                Calendar.getInstance().getTime(),
                endCalendar.getTime(),
                new X500Name("CN=cliente"),
                SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded())
        );
        ContentSigner contentSigner = new JcaContentSignerBuilder("HMACSHA512 with RSA").build(keyPair.getPrivate());
        X509CertificateHolder x509CertificateHolder = x509v3CertificateBuilder.build(contentSigner);
        return (new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(x509CertificateHolder));
    }

    private boolean verificarCertificado(X509Certificate certificado) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException {
        try {
            this.publicKeyServidor = certificado.getPublicKey();
            certificado.verify(this.publicKeyServidor);
            return true;
        } catch (SignatureException e){
            e.printStackTrace();
            return false;
        } catch (InvalidKeyException e2){
            e2.printStackTrace();
            return false;
        }
    }

    private SecretKey descifrarLlaveServidorCliente(byte[] llave) throws Exception{
        byte[] descifrado;

        Cipher cifrado = Cipher.getInstance("RSA");
        cifrado.init(cifrado.DECRYPT_MODE, privateKey);
        descifrado = cifrado.doFinal(llave);
        SecretKey key = new SecretKeySpec(descifrado, "AES");
        return key;

    }

    private String descifrarMensajeSimetricoServidorCliente(byte[] textoCifrado) throws Exception{
        byte[] textoDescifradoEnBytes;
        Cipher cifrado = Cipher.getInstance(PADDING_AES);
        cifrado.init(cifrado.DECRYPT_MODE, llaveCompartida);
        textoDescifradoEnBytes = cifrado.doFinal(textoCifrado);
        return toHexString(textoDescifradoEnBytes);
    }

    private byte[] cifrarSimetrico(Key llavePublicaServidor, String mensaje) throws Exception{
        byte[] mensajeCifrado;

        Cipher cifrado = Cipher.getInstance("RSA");
        byte[] mensajeBytes = toByteArray(mensaje);

        cifrado.init(Cipher.ENCRYPT_MODE,llavePublicaServidor);
        mensajeCifrado = cifrado.doFinal(mensajeBytes);
        return mensajeCifrado;
    }

    private byte[] cifrarSimetricamente(Key llaveCompartida, String mensaje) throws Exception{
        byte[] mensajeCifrado;
        Cipher cifrado = Cipher.getInstance(PADDING_AES);
        byte[] mensajeClaro = toByteArray(mensaje);

        cifrado.init(Cipher.ENCRYPT_MODE, llaveCompartida);
        mensajeCifrado = cifrado.doFinal(mensajeClaro);
        return mensajeCifrado;
    }

    public void run() throws Exception{
        while(true){
            System.out.println("Estableciendo Conexion Con El Servidor...");
            pw.println(HOLA);
            String msjServidor = brServer.readLine();

            if(msjServidor.equals(OK)){
                keyPairCliente = generarLlaves();
                X509Certificate certificado = generarCertificado(keyPairCliente);
                byte[] certificadoBytes = certificado.getEncoded();
                String certificadoString = toHexString(certificadoBytes);
                pw.println(certificadoString);
            }
        }
    }

}
