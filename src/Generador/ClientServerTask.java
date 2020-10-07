package Generador;

import Client.Main;
import Server.ServerThread;
import uniandes.gload.core.Task;

import java.io.FileWriter;
import java.io.IOException;

public class ClientServerTask extends Task {
    private FileWriter fwf;

    public ClientServerTask(){
        super();
        try {
            fwf = new FileWriter(ServerThread.fallas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void execute() {
        try {
            Main.run(); //Este se encarga de ejecutar el servidor Seguro o Inseguro. El seguro y el inseguro tienen el mismo nombre Main.run()a
            success();
            Generator.incCounter();

            if (Generator.counter == Generator.numberOfTasks) {
                fwf.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Override
    public synchronized void fail() {
        System.out.println(Task.MENSAJE_FAIL);
        try {
            fwf.write("Error\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void success(){
        System.out.println(Task.OK_MESSAGE);
        try {
            fwf.write("Bien\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
