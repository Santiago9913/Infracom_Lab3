package Generador;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {
    private LoadGenerator generator;
    public static int counter = 0;
    public static int numberOfTasks;

    public Generator() {
        Task work = createTask();
        numberOfTasks = 10; //# tareas
        int gapBetweenTasks = 500; //Milisegundos
        generator = new LoadGenerator(
                "Client - Server Load Test",
                numberOfTasks,
                work,
                gapBetweenTasks);
        generator.generate();
    }

    private Task createTask(){
        return new ClientServerTask();
    }

    public static void incCounter(){
        counter++;
    }

    public static void main (String ... args){
        @SuppressWarnings("unused")
        Generator gen = new Generator();
    }

}
