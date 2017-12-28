/*
 * Author : Pragati Shrivastava
 * Date : July 2017
 * Description : This class contains methods to convert JSON to String Array, then TFIDF vectors are calculated. Cosine score is calculated from TFIDF vectors. The
 *               Question/Answer with maximum score is returned.
 * jsonToString() : Method to convert JSON with QA to String Array of documents
 * getTF(String text) : Method to return HashMap of Terms and TF
 * getDF(String[] docs) : Method to return HashMap of Terms and DF
 * stringToWordVec(String txt, HashMap<String, Double> DF) : Method to Convert Document to WordVector
 * getCosineSimilarity(double[] v2, double[] v1) : Method to calculate Cosine Similarity of vectors
 * getCosineScore(String txt, HashMap<String, Double> DF) : Method to create a table with ID, Q/A, QA Text, Cosine Score for query and all docs
 * selectQuestion(ArrayList<CosineValueTable> cosineTable) :  Method to select the Question/Answer with greatest Cosine Score. The threshold value is 0.5 
 */

package dm.utils.textalgs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import dm.utils.StringUtils;

import java.util.Set;

public class CalculateTFIDF {

	protected static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public static int docsSize;

	public CalculateTFIDF() {

	}

	/* Method to return HashMap of Terms and TF */

	public HashMap<String, Double> getTF(String text) {
		HashMap<String, Double> termsFreq = new HashMap<String, Double>();
	    /* Find stemmed text and then find TF of stemmed words */
		String stemmedText = StringUtils.stemText(text);
		String terms[] = stemmedText.split("[\\s]");
		double termFreqValue;
		double count;

		for (int i = 0; i < terms.length; i++) {
			count = 0;
			termFreqValue = 0;
			for (int j = 0; j < terms.length; j++) {
				if (terms[j].equalsIgnoreCase(terms[i])) {
					count++;
				}
			}
            termFreqValue = count / terms.length;
			if (!(termsFreq.containsKey(terms[i])))
				termsFreq.put(terms[i], termFreqValue);
		}
	 return termsFreq;
   }

	/* Method to return HashMap of Terms and DF */
    public HashMap<String, Double> getDF(String[] docs) {
		HashMap<String, Double> docsFreq = new HashMap<String, Double>();
		double freq;
		docsSize = docs.length;
		List<String> allTerms = new ArrayList<String>();
        for (String singleDoc : docs) // To get all the terms in documents
		{
			String docTerms[] = singleDoc.split("\\s");
			for (String singleTerm : docTerms) {
				if (!allTerms.contains(singleTerm))
					allTerms.add(singleTerm);
			}
		}
        for (int i = 0; i < allTerms.size(); i++) // To calculate DF of all the terms
        {
		  freq = 0;
		  for (int j = 0; j < docs.length; j++)
		  {
		    String dt[] = docs[j].split("\\s");
			for (String word : dt) 
			{
			 if (allTerms.get(i).equalsIgnoreCase(word))
			 {
			  freq++;
			  break;
			 }
			}
		   }

			if (!(docsFreq.containsKey(allTerms.get(i)))) {
				docsFreq.put(allTerms.get(i).toLowerCase(), freq);
			}
		}

		LOG.info("DF is" + Arrays.asList(docsFreq));
		return docsFreq;

	}

	/* Method to Convert Document to WordVector */

	public double[] stringToWordVec(String txt, HashMap<String, Double> DF) {
		String txtDFKey;
		double txtDFValue;
		double txtIDFValue;
		double[] wordVecs = new double[DF.size()];
		int i = 0;
		int count = 0;
		HashMap<String, Double> termFreq = getTF(txt);
		Set<Entry<String, Double>> dfEntrySet = DF.entrySet();
		Iterator<Entry<String, Double>> dfEntrySetIterator = dfEntrySet.iterator();
		while (dfEntrySetIterator.hasNext()) {
			Entry<String, Double> termEntry = dfEntrySetIterator.next();
			txtDFKey = (String) termEntry.getKey();
			txtDFValue = (Double) termEntry.getValue();
			double tfidfValue = 0;

			Set<Entry<String, Double>> tfEntrySet = termFreq.entrySet();
			Iterator<Entry<String, Double>> tfEntrySetIterator = tfEntrySet.iterator();

			while (tfEntrySetIterator.hasNext()) {
				count = 0;
				Entry<String, Double> dfEntry = tfEntrySetIterator.next();
				if (txtDFKey.equalsIgnoreCase((String) dfEntry.getKey())) {
					count++;
					txtIDFValue = 1 + Math.log(docsSize / txtDFValue);
                    tfidfValue = (Double) dfEntry.getValue() * txtIDFValue;
                    break;
				}
           }
           if (count == 0) {
				wordVecs[i] = 0;
			} else {
				wordVecs[i] = tfidfValue;
			}
			i++;
         }
		
    return wordVecs;
   }

	/* Method to calculate Cosine Similarity of vectors */

	public double getCosineSimilarity(double[] v2, double[] v1) {
		double dotProductVec = 0.0;
		double valueVector1 = 0.0;
		double valueVector2 = 0.0;
		double cosineSimilarity = 0.0;

		for (int i = 0; i < v2.length; i++) {
			dotProductVec += v1[i] * v2[i];
			valueVector1 += Math.pow(v1[i], 2);
			valueVector2 += Math.pow(v2[i], 2);
        }

		valueVector1 = Math.sqrt(valueVector1);
		valueVector2 = Math.sqrt(valueVector2);
		if (!(valueVector1 == 0.0 || valueVector2 == 0.0)) {
			cosineSimilarity = dotProductVec / (valueVector1 * valueVector2);
		} 
		else {
			return 0.0;
		}
      return cosineSimilarity;
    }

}
