package Server;

import Client.Client;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int nThreads = 25;

    private static ServerSocket ss;
    private static X509Certificate certificado;
    private static KeyPair keyPair;

    private static KeyPair generarKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
        kpGen.initialize(1024, new SecureRandom());
        return kpGen.generateKeyPair();
    }

    private static X509Certificate generarCertificado(KeyPair keyPair, String host)
            throws OperatorCreationException, CertificateException {
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.YEAR, 10);
        X509v3CertificateBuilder x509v3CertificateBuilder =
                new X509v3CertificateBuilder(new X500Name("CN="+host),
                        BigInteger.valueOf(1),
                        Calendar.getInstance().getTime(),
                        endCalendar.getTime(),
                        new X500Name("CN="+host),
                        SubjectPublicKeyInfo.getInstance(keyPair.getPublic()
                                .getEncoded()));
        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA")
                .build(keyPair.getPrivate());
        X509CertificateHolder x509CertificateHolder =
                x509v3CertificateBuilder.build(contentSigner);
        return new JcaX509CertificateConverter().setProvider("BC")
                .getCertificate(x509CertificateHolder);
    }


        public static void main(String[] args) throws Exception {
            Scanner sc = new Scanner(System.in);
            System.out.println("Ingrese El HOST: ");
            String host = sc.next();

            System.out.println("Ingrese El PORT: ");
            int port = sc.nextInt();


            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            keyPair = generarKeyPair();
            certificado = generarCertificado(keyPair, host);

            ServerThread.init(certificado,keyPair);
            ss = new ServerSocket(port);
            System.out.println("[!] Iniciando Servidor En " + host +":" +port);

            ExecutorService pool = Executors.newFixedThreadPool(nThreads);

            for(int i = 0; true; i++){
                try{
                    Socket socketC = ss.accept();
                    pool.execute(new ServerThread(socketC, i));

                } catch (IOException e) {
                    System.out.println("Error creando el socket cliente.");
                    e.printStackTrace();
                }
            }


    }


}
