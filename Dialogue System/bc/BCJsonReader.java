package sys.bc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import dm.infostate.InformationState;
import dm.infostate.grounding.Fact;
import dm.utils.StringUtils;
import dm.utils.textalgs.CalculateTFIDF;
import java.util.Set;


public class BCJsonReader {
	
	protected static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private InformationState informationState;
	public String[] audioFiles = null;
	private JSONObject jsonObj;

	public BCJsonReader(InformationState is) {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new BufferedReader(new InputStreamReader(
	        		this.getClass().getClassLoader().getResourceAsStream("BreastCancerDialogueData.json")))); // Object created
			
			jsonObj = (JSONObject)obj; // Object converted to JSONObject
		} catch (Exception e){
			e.printStackTrace();
		}
    }
	
	public BCJsonReader(){
		super();
	}
		
   /* Method to convert JSON with QA to String Array of documents */
    public String[] jsonToString(){
		List<String> docsList = new ArrayList<String>();
		String[] docs = null;
		String stemDoc;
		JSONObject questionInside ;
		JSONArray altQues ;
		Object answerObj ;
		JSONArray answerArray;
		String answer;
		try
		{
			LOG.info("********Reading JSON and adding to array********");
	        
            JSONArray questions = (JSONArray)jsonObj.get("questions");  // Reading the array
            
            for(Object question : questions)  // Iterating JSON Array 
            { 
                questionInside = (JSONObject)question;
            	altQues = (JSONArray)questionInside.get("question_alternatives");  // Reading array of Questions
            	for(Object alt : altQues )
            	{
            		stemDoc = StringUtils.stemText((String)alt); // Stem the Question
            		docsList.add(stemDoc);
                }
            	
            	/* Updating code to check is answer is JSONArray*/
                answerObj = questionInside.get("question_answer");
            	if(answerObj instanceof JSONArray)
            	{
            		answerArray = (JSONArray)answerObj;
            		for(Object ans : answerArray)  // Add answer array
            		{
            			stemDoc = StringUtils.stemText((String)ans); // Stem the Answer
                		docsList.add(stemDoc);
            		}
            	}
                else
            	{
            		answer = (String)answerObj;
                    stemDoc = StringUtils.stemText(answer);    // Stem the Answer
                    docsList.add(stemDoc);
            	}
             }
          docs = docsList.toArray(new String[docsList.size()]);
       }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		
		return docs;
    }
	
/* Method to create initial table with ID, Q/A, QA Text, Cosine Score for query and all docs*/
	
	public ArrayList<CosineValueTable> getJsonValues(){
 
	    ArrayList<CosineValueTable> jsonValues = new ArrayList<CosineValueTable>();
	    JSONArray questions ;
	    JSONObject questionInside ;
	    JSONArray altQues ;
		Object answerObj ;
		JSONArray answerArray;
		String answer;

	    try
	    {
            //Reading the array
            questions = (JSONArray)jsonObj.get("questions");
            for(Object question : questions)
            {
                questionInside = (JSONObject)question;
                Long id = (Long)questionInside.get("question_id");
                altQues = (JSONArray)questionInside.get("question_alternatives");
            	
            	for(Object alt : altQues )
            	{
            		jsonValues.add(new CosineValueTable(id,"Q",alt.toString(),0.0));
            	}
            	
            	answerObj = questionInside.get("question_answer");
            	if(answerObj instanceof JSONArray)    // If Answer is array 
            	{
            		answerArray = (JSONArray)answerObj;
            		if(questionInside.containsKey("type"))   // If answer is type audio
            		{
            			for(Object ans : answerArray)
                		{
                			jsonValues.add(new CosineValueTable(id,"S",ans.toString(),0.0)); // Audio file name 
                		}
            		}
            		else
            		{
            			for(Object ans : answerArray)
                		{
                			jsonValues.add(new CosineValueTable(id,"AA",ans.toString(),0.0)); // Array of answers
                		}
            		 }
                   }
                else
            	{
            		answer = (String)answerObj;
            		jsonValues.add(new CosineValueTable(id,"A",answer,0.0));
            	}
              }
         }
	    catch(Exception e)
        {
            e.printStackTrace();
        }
     return jsonValues;
    }
	
	
/* Method to update Cosine Score for query and all docs in the Json Values read */
	
	public ArrayList<CosineValueTable> getCosineScore(String txt, HashMap<String, Double> DF, ArrayList<CosineValueTable> cosineTable ){
		
		CalculateTFIDF calculator = new CalculateTFIDF();
        LOG.info("********Converting Query to TFIDF Word Vector********");
        
	    double[] tfidfQuery = calculator.stringToWordVec(txt, DF);  
	    double[] tfidfDoc;
	    double cosineScore;
	    String question = null;

	    try
	    {
          for(CosineValueTable score : cosineTable)
            {
               question = score.getText();
               tfidfDoc = calculator.stringToWordVec(question, DF);   // Convert Document to TFIDF Vector
               cosineScore = calculator.getCosineSimilarity(tfidfQuery, tfidfDoc);   // Find Cosine Score
               score.setCosineScore(cosineScore);  // Set Cosine Score in the read json values
            }
         }
       catch(Exception e)
        {
            e.printStackTrace();
        }
      return cosineTable;
    }
		
/* Method to select the Question/Answer/Audio with greatest Cosine Score. The threshold value is 0.5 */
	
	public String selectQuestion(ArrayList<CosineValueTable> cosineTable){
		
		double maxCosine = 0.0;
		double cosine = 0.0;
		String question = null ;
		String type = "";
		Long id = null;
		String qa;
		for(CosineValueTable score : cosineTable)  // This loop will only check Questions > 0.5
		{
			qa = score.getQa();
			if(qa.equals("Q"))
			{
				cosine = score.getCosineScore();
				if(cosine > 0.5)
				{
				if(cosine > maxCosine)
				{
				  maxCosine=cosine;
				  question = score.getText();
				  id = score.getId();
				  type = "Q";
                 }
				}
			  }
            }
		if(maxCosine == 0.0)    // After end of first loop if maxcosine is not found then this loop will check Answers > 0.5
		{
			for(CosineValueTable score : cosineTable)
			{
				qa = score.getQa();
				if(qa.equals("A") || qa.equals("AA"))
				{
				  cosine = score.getCosineScore();
					if(cosine > 0.5)
					{
					if(cosine > maxCosine)
					{
						maxCosine=cosine;
						question = score.getText();
                     }
					}
				  }
              }
		}
		
	//	LOG.info("Question Id with max cosine is " + id);
		/* Added part to get Answers*/
		if(type.equals("Q"))
		{
			for(CosineValueTable score : cosineTable)  // Get Id of the Question
			{
				if(score.getId() == id && score.getQa().equals("A"))
				{ 
					question = score.getText();
				    break;	
				}
                if(score.getId() == id && ( (score.getQa().equals("AA")) || (score.getQa().equals("S"))))
               {
					question = getRandomReply(score.getId(), cosineTable);
               }	
             }
         }
        if(maxCosine == 0.0)    // If answer is also not found then ask to rephrase the question
		{
			question = "Please rephrase the question";
		}	
		
	LOG.info("Maximum Cosine is " + maxCosine );
	return question;
   }
	
   // Method to return Random Audio
    public static String getAudioFile(Long qId, ArrayList<CosineValueTable> cosineTable )
    {
    	String[] audioFilesList = null;
    	audioFilesList = new String[20];
    	int count = 0;
    	Random generator = new Random();
    	String audio;
    	int j = 0;
    	
    	for (int i = 0 ; i<cosineTable.size(); i++)
    	{
    		if(cosineTable.get(i).getId() == qId && cosineTable.get(i).getQa().equals("S"))
    		{
    			audioFilesList[j] = cosineTable.get(i).getText();
    			count++;
    			j++;
    		}
         }
        int randomIndex = generator.nextInt(count);
        audio = audioFilesList[randomIndex];
     return audio;
    }
    
 // Method to return Random Audio or Random Answer from array
    public static String getRandomReply(Long qId, ArrayList<CosineValueTable> cosineTable )
    {
    	ArrayList<String> answersList = new ArrayList<>();
    	int count = 0;
    	Random generator = new Random();
    	String answer;
    	int j = 0;
        for (int i = 0 ; i<cosineTable.size(); i++)
    	{
    		if(cosineTable.get(i).getId() == qId && ((cosineTable.get(i).getQa().equals("S")) ||  (cosineTable.get(i).getQa().equals("AA"))))
    		{
    			answersList.add(j, cosineTable.get(i).getText());
    			count++;
    			j++;
    		}
        }
        int randomIndex = generator.nextInt(count);
        answer = answersList.get(randomIndex);
      return answer;
     }
	
}
