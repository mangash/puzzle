package mangash.git.puzzle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.PriorityBlockingQueue;

public class Main {
	
	public static void main(String[] args) {
		if (args.length != 1)
		{
			System.out.println ("Wrong number of paramaters passed. Expected: 1");
			return;
		}
		String path = args [0];
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles==null)
		{
			System.out.println ("No such directory: " + path + "\nExiting...\n");
			return;
		}
		if (listOfFiles.length==0)
		{
			System.out.println ("No files found in path: " + path + "\nExiting...\n");
			return;
		}
		Properties stopWords=new Properties();
		try {
			System.out.println ("Reading stop words from file...");
			FileInputStream fis = new FileInputStream("./stopwords_en.txt");
			stopWords.load(fis);
			fis.close();
			System.out.println ("Reading stop words completed.");
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find stop words file. Continuing without it...");
			stopWords=null;
		} catch (IOException e) {
			System.out.println("Unable to read stop words file. Continuing without it...");
			stopWords=null;
		}
		
		ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> tf = new ConcurrentHashMap<String, ConcurrentHashMap<String, Double>>();
		ConcurrentHashMap<String, Double> idf = new ConcurrentHashMap<String, Double>(); 
		ExtractKeyTerms ekt = new ExtractKeyTerms(listOfFiles, 0, listOfFiles.length-1, tf, idf, stopWords);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(ekt);
		
		for (Entry<String, Double> entry : idf.entrySet())
		{
			entry.setValue(Math.log(listOfFiles.length/entry.getValue()));
		}
		
		PriorityBlockingQueue<MinTerm> corpusKeyTerms=new PriorityBlockingQueue<MinTerm>();
		for (File file : listOfFiles)
		{
			PriorityBlockingQueue<MaxTerm> docKeyTerms=new PriorityBlockingQueue<MaxTerm>();
			for (Entry<String, Double> entry : tf.get(file.getName().toString()).entrySet())
			{
				String key=entry.getKey();
				Double termTf=entry.getValue();
				Double termIdf=idf.get(key);
				docKeyTerms.add(new MaxTerm(key, termTf*termIdf));
			}
			System.out.println("Key terms for file : " + file.getName());
			for (int i=0;i<5 && !docKeyTerms.isEmpty();i++)
			{
				System.out.println(docKeyTerms.poll());
			}
			System.out.println();
		}
		
		for (Entry<String, Double> entry : idf.entrySet())
		{
			String key=entry.getKey();
			Double termIdf=idf.get(key);
			corpusKeyTerms.add(new MinTerm(key, termIdf));
		}
		
		System.out.println("Key terms for entire corpus :");
		for (int i=0;i<9 && !corpusKeyTerms.isEmpty();i++)
		{
			System.out.println(corpusKeyTerms.poll());
		}
	}
}
