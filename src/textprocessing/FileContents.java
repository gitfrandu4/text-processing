package textprocessing;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
public class FileContents {
    
    // Cola de ristras que almacenan la información textual de los ficheros
    private volatile Queue<String> queue;
    private final int maxFiles;     // Número máximo de ristras (una ristra para cada 
    private final int maxChars;     // Número máximo de caracteres por ristra. 
    
    private volatile int nChars;    // Número de carácteres en FileContents
    
    private volatile int registerCount;
    private volatile boolean closed;
    
    /**
     * Constructor. 
     * @param maxFiles Número máximo de ristras, cada una de las cuales contendrá toda la
     *                  información textual de uno de los ficheros.
     * @param maxChars Tamaño máximo de caracteres almacenables 
     */
    public FileContents(int maxFiles, int maxChars) {
        this.maxChars = maxChars;
        this.maxFiles = maxFiles;
        
        queue = new LinkedList<>();
        registerCount=0;
        nChars=0;
        closed=false;
    }
    
    /**
     * Se llama para indicar que hay un nuevo FileReader usando el objeto
     */
    public synchronized void registerWriter() {
        registerCount++;
        //System.out.println("Prueba - FileContents::registerWriter => " + registerCount);
    }
    
    /**
     * Se llama para indicar que un FileReader ha dejado de producir contenido
     */
    public synchronized void unregisterWriter() {
        registerCount--;  
        if(registerCount==0)
            closed=true;
        //System.out.println("Prueba - FileContents::unregisterWriter => " + registerCount);
    }
    
    /**
     * Llamado por hilo FileReader para añadir contenido de un fichero.
     * @param contents Contenido textual de un fichero a añadir
     */
    public synchronized void addContents(String contents) {

        // Si se supera el límite de ficheros ó hay más de un fichero y se supera el 
        // límite de caracteres => wait
        while( queue.size() > maxFiles || 
                (queue.size() > 0 && nChars+contents.length() > maxChars)) {
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println("FileContents::addContents(Interrupted Exception): I wasn't done!");
            }
        }
        
        // Si hay espacio añade el contenido
        queue.add(contents);
        nChars+=contents.length();
        notifyAll();   
    }
    
    /**
     * Llamado por hilo FileProcessor para extraer el contenido de un fichero. 
     * @return Cadena con el contenido del fichero
     */
    public synchronized String getContents() {
        String content = "";
        
        // Mientras la cola de ristras está vacía
        while(queue.isEmpty()) {
            
            // Si no hay contenido ni FileReaders registrados => null
            if (queue.isEmpty() && closed) {
                return null;
            }
            
            // Si no hay archivos espera hasta que cambie la situación.
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println("FileContents::addContents(Interrupted Exception): " + 
                                    ex.getMessage());
            }
        }
        
        // Cuando hay elementos, devolvemos el contenido de la cabeza de la cola
        try {
            content = queue.remove();
        } catch (NoSuchElementException ex) {
            System.out.println("Error FileContents::getContent(No Such Element) " + 
                                ex.getMessage());
        }
        nChars-=content.length();
        return content;
    }
}