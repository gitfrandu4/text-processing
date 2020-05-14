package textprocessing;
import java.util.*;
import java.io.*;

public class TextProcessing{
    public static void main(String[] args) {
        FileNames fileNames= new FileNames();
        FileContents fileContents= new FileContents(30, 100 * 1024);
        WordFrequencies wordFrequencies= new WordFrequencies();
        
        /*
        Cree e inicie los hilos AQUÍ
        2 FileReader y 3 FileProcessor
        */
        FileReader fileReader1;
        FileReader fileReader2;
        
        FileProcessor fileProcessor1;
        FileProcessor fileProcessor2;
        FileProcessor fileProcessor3;
        
        fileReader1 = new FileReader(fileNames, fileContents);
        fileReader2 = new FileReader(fileNames, fileContents);
        fileReader1.start();
        fileReader2.start();
        
        fileProcessor1 = new FileProcessor(fileContents, wordFrequencies);
        fileProcessor2 = new FileProcessor(fileContents, wordFrequencies);
        fileProcessor3 = new FileProcessor(fileContents, wordFrequencies);
        fileProcessor1.start();
        fileProcessor2.start();
        fileProcessor3.start();
       
        // ---------------------------------------------------------------------
        
        Tools.fileLocator(fileNames, "datos");
        fileNames.noMoreNames();
        
        /*
        Esperar a que terminen los hilos creados
        */
        try {
            fileReader1.join();
            fileReader2.join();
            
            fileProcessor1.join();
            fileProcessor2.join();
            fileProcessor3.join();
        } catch (InterruptedException ex) {
            System.out.println("Error Main::main(Interrupted Exception) " + 
                                ex.getMessage());
        }
        
        // ---------------------------------------------------------------------
        
        Tools.wordSelector(wordFrequencies.getFrequencies()).forEach((palabra) -> {
            System.out.println(palabra);
        });
    }
}

class Tools {
    public static void fileLocator(FileNames fn, String dirname){
        File dir = new File(dirname);
        if ( ! dir.exists() ){
            System.out.println("nope");
            return;
        }
        if ( dir.isDirectory() ) {
            String[] dirList = dir.list();
            for ( String name: dirList ) {
                if ( name.equals(".") || name.equals("..") ) {
                    continue;
                }
                fileLocator(fn, dir + File.separator + name);
            }
        } else {
            fn.addName(dir.getAbsolutePath());
            System.out.println(dir.getAbsolutePath());
        }
    }
    public static class Order implements Comparator< Map.Entry<String,Integer> > {
        public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
            if ( o1.getValue().compareTo(o2.getValue()) == 0 ) {
                return o1.getKey().compareTo(o2.getKey());
            }
            return o2.getValue().compareTo(o1.getValue());
        }
    }
    
    public static List<String> wordSelector(Map<String,Integer> wf){
        Set<Map.Entry<String,Integer>> set=wf.entrySet();
        Set<Map.Entry<String,Integer>> orderSet= new TreeSet<Map.Entry<String,Integer>>(
            new Order());
        orderSet.addAll(set);
        List<String> l = new LinkedList<String>();
        int i=0;
        for ( Map.Entry<String,Integer> pair: orderSet){
            l.add(pair.getValue() + " " + pair.getKey() );
            if (++i >= 10 ) break;
        }
        return l;
    }

    public static  String getContents(String fileName){
        StringBuilder text = new StringBuilder();
		try {
		    FileInputStream fis = new FileInputStream( fileName );
	        BufferedReader br = new BufferedReader( new InputStreamReader(fis, "ISO8859-1") );
			String line;
			while (( line = br.readLine()) != null) {
				text.append(line);
				text.append("\n");
			}
		} catch (IOException e) {
			return "Error: " + e.getMessage();
		}
        return text.toString();
    }
    
}