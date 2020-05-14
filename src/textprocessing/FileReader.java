package textprocessing;

public class FileReader extends Thread {
    
    private final FileNames fn;
    private final FileContents fc;
    
    /**
     * Constructor
     * @param fn Objeto FileNames de donde toma el hilo los nombres de ficheros a procesar. 
     * @param fc Objeto FileContents donde añadimos el contenido de los ficheros.
     */
    public FileReader(FileNames fn, FileContents fc){
        this.fn = fn;
        this.fc = fc;
        
        // Añadimos un nuevo FileReader usando FileContents
        fc.registerWriter();
    }
    
    @Override
    public void run() {
        /**
         * Obtiene iterativamente un nombre de fichero de un FileNames
         * Termina si FileNames le devuelve null.
         */
        String name = fn.getName();
        
        while(name != null) {      
            // Lee su contenido
            String data = Tools.getContents(name);
            
            // Añadimos la ristra a FileContents
            fc.addContents(data);
            
            // Obtiene el nombre de un nuevo archivo
            name = fn.getName();
        }
        // Indicamos que el FileReader deja de producir contenido. 
        fc.unregisterWriter();
    }
}