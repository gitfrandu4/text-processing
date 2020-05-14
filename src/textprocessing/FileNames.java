package textprocessing;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class FileNames {
    // queue almacena los nombres de los ficheros
    private volatile Queue<String> queue;
    private volatile boolean noMore = false;
    
    // Constructor (no requiere atributo synchronized)
    public FileNames() {
        queue = new LinkedList<>();
        //nFiles = 0;
    }
    
    /**
     * Almacena un nuevo nombre de fichero en el objeto.
     * @param fileName Nombre de fichero a almacenar.
     */
    public synchronized void addName(String fileName) {
        queue.add(fileName);
        notifyAll();
    }
    
    /**
     * Extrae del objeto y devuelve un nombre de fichero.
     * @return Devuelve null si no hay ficheros disponibles y además se ha llamado a noMoreNames().
     */
    public synchronized String getName() {
        String filename = "";
        
        // Si no hay nombres de ficheros disponibles =>
        while (queue.isEmpty()){
            
            // Comprobamos si no hay más ficheros y si ya no se admiten más => null
            if (noMore==true && queue.isEmpty())
                return null;
            
            // En caso de admitir más esperamos a que se introduzcan nuevos ficheros
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println("FileNames::getName(Interrupted Exception): I wasn't done!");
            }
        }
        
        // Si hay nombres de fichero disponibles => return filename
        try {
            filename = queue.remove();
        } catch (NoSuchElementException ex) {
            System.out.println("Error FileNames::getName(No Such Element) " + 
                                ex.getMessage());
        }
        notifyAll();
        return filename;
    }
    
    /**
     * Da lugar a que el objeto no admita más nombres de fichero. 
     */
    public synchronized void noMoreNames() {
        noMore = true;
        notifyAll();
    }
}