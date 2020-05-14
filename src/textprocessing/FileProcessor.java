package textprocessing;

import java.util.HashMap;
import java.util.Map;

public class FileProcessor extends Thread {
    
    private final FileContents fc;
    private final WordFrequencies wf;
    private Map<String, Integer> wordCount;
    
    /**
     * Constructor
     * @param fc
     * @param wf   
     */
    public FileProcessor(FileContents fc, WordFrequencies wf){
        this.fc=fc;
        this.wf=wf;
        this.wordCount = new HashMap<>();
    }
    
    @Override
    public void run() {
        // Lee iterativamente el contenido de fichero de un FileContents y lo procesa
        // hasta que devuelve null.
        String contents = fc.getContents();
        
        while(contents!=null){
            // Procesamos el contenido y lo almacenamos en un vector. 
            contents = contents.toLowerCase();
            contents = contents.replaceAll("\\p{Punct}", " ");  // Eliminamos los signos de puntuación
            String [] c = contents.split("\\s+");               // Dividimos el contenido
            
            for (String word : c) {
                wordCount.put(word, wordCount.getOrDefault(word, 0)+1);
            }
            
            contents=fc.getContents();
        }
        
        // Contabilizamos la frecuencia de cada palabra
        wf.addFrequencies(wordCount);   // Almacenamos el resultado
    }
}