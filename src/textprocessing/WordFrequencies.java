package textprocessing;
import java.util.Map;
import java.util.HashMap;

public class WordFrequencies {

    private volatile Map<String, Integer> wordFreq;
    
    /**
     * Constructor
     */
    public WordFrequencies() {
        wordFreq = new HashMap<>();
    }
    
    /**
     * Añade al objeto actual la información de palabras/frecuencias pasadas
     * @param f Estructura HashMap que contiene la información a almacenar
     */
    public synchronized void addFrequencies(Map<String,Integer> f){
        f.forEach((k, v) -> wordFreq.put(k, wordFreq.getOrDefault(k, 0)+v));
    }
    
    /**
     * @return Devuelve las parejas de palabras/frecuencias acumuladas
     */
    public synchronized Map<String,Integer> getFrequencies(){
        return wordFreq;
    }
}
