package mangash.git.puzzle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;


public class ExtractKeyTerms extends RecursiveAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -305893147736165018L;
	final File[] listOfFiles;
	int startIndex;
	int endIndex;
	final ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> tf;
	final ConcurrentHashMap<String, Double> idf;
	final Properties stopWords;
	double maxTermOccurences=1;
	double numberOfDocs;
	
	ExtractKeyTerms(File[] listOfFiles, 
			int startIndex, 
			int endIndex, 
			ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> tf,
			ConcurrentHashMap<String,Double> idf,
			Properties stopWords)
	{
		this.listOfFiles=listOfFiles;
		this.numberOfDocs=listOfFiles.length;
		this.startIndex=startIndex;
		this.endIndex=endIndex;
		this.tf=tf;
		this.idf=idf;
		this.stopWords=stopWords;
	}
	
	@Override
	protected void compute() {
		if (startIndex==endIndex)
		{
			System.out.println("Processing file " + listOfFiles[startIndex].getName());
			computeDirectly();
			System.out.println("Finished processing file " + listOfFiles[startIndex].getName());
			return;
		}
		invokeAll(new ExtractKeyTerms(listOfFiles, startIndex, startIndex+(endIndex-startIndex)/2, this.tf, this.idf, this.stopWords),
				new ExtractKeyTerms(listOfFiles, startIndex+(endIndex-startIndex)/2+1,endIndex, this.tf, this.idf, this.stopWords));
	}
	
	protected void computeDirectly()
	{
		mapFileTerms(listOfFiles[startIndex]);
	}
	
	
	private void mapFileTerms(File txtFile)
	{
		if (txtFile.isFile()) 
		{
			tf.put(txtFile.getName(), new ConcurrentHashMap<String,Double>());
			FileInputStream is=null;
			try {
				is = new FileInputStream(txtFile);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			BufferedReader br=new BufferedReader(new InputStreamReader(is,Charset.forName("UTF-8")));
			String lineInput= "";
			try {
				while ((lineInput=br.readLine())!=null)
				{
					for (String termName : lineInput.split("\\s+"))
					{
						String cleanTermName=termName.replaceAll("[^a-zA-Z\\- ]", "").toLowerCase().trim();
						if (!(cleanTermName.isEmpty())&& !stopWords.containsKey(cleanTermName))
						{
							updateTf(txtFile.getName(), cleanTermName);
						}
					}
				}
				normalizeTf(txtFile.getName());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void updateTf(String docName, String termName)
	{
		ConcurrentHashMap<String, Double> docMap = tf.get(docName);
		if (docMap.containsKey(termName.toString()))
		{
			Double currentValue=docMap.get(termName.toString());
			docMap.put(termName, currentValue+1);
			this.maxTermOccurences=Math.max(this.maxTermOccurences, currentValue+1);
		}
		else
		{
			docMap.put(termName, 1.0);
		}
	}
	
	private void normalizeTf(String docName)
	{
		ConcurrentHashMap<String,Double> docMap = tf.get(docName.toString());
		for (Entry<String, Double> entry : docMap.entrySet())
		{
			updateIdf(entry.getKey());
			entry.setValue(0.5+(0.5*entry.getValue())/this.maxTermOccurences);
		}
	}
	
	private void updateIdf(String termName)
	{
		if (idf.containsKey(termName.toString()))
		{
			Double currentValue=idf.get(termName.toString());
			idf.put(termName, currentValue+1);
		}
		else
		{
			idf.put(termName, 1.0);
		}
	}
}
