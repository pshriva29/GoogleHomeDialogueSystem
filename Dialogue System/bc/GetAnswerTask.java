package sys.bc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


import dm.logger.DMLogger;
import dm.tasks.Task;
import dm.utils.textalgs.CalculateTFIDF;
import dm.infostate.InformationState;
import dm.infostate.grounding.Fact;

public class GetAnswerTask extends Task {

private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	ArrayList<String> isFields; // Info State fields necessary to satisfy this task
	String regexpValidSpaces = InformationState.AGENT_BELIEFS+"|"+
							   InformationState.COMMON_GROUND+"|"+
							   InformationState.CONVERSATION_BELIEFS+"|"+
							   InformationState.CONVERSATION_BELIEFS;
	
	public String inFields,outField;
	public HashMap<String, Double> df;
    public ArrayList<CosineValueTable> jsonValues;

	public GetAnswerTask(String name,Properties properties)
	{
		super(name,properties);
		LOG.setLevel(DMLogger.DEFAULT_LOG_LEVEL);
	}

	@Override
	public void setMandatoryFields() {
		mandatoryFields = new String[2];
		mandatoryFields[0] = "inFields"; //which field is the question going in (in the IS) space:field
	    mandatoryFields[1] = "outField"; //which field is the message going in (in the IS) space:field
	}


	@Override
	public void init() {
		isFields = new ArrayList<String>();
		inFields = properties.getProperty("inFields");
		outField = properties.getProperty("outField");
		
		if(isValidISField(inFields))
			this.isFields.add(inFields);
		else
			System.out.println("Namespace ["+inFields+"] is not a valid namespace or not in the right format");
		if(!isValidISField(outField))
			LOG.warning("Namespace ["+outField+"] is not a valid namespace or not in the right format");
		
		/* Json Reader Methods */
		CalculateTFIDF calculator = new CalculateTFIDF();
		BCJsonReader jsonReader = new BCJsonReader(this.is);
		LOG.info("********Converting JSON to Array of documents********");
	    String[] documents = jsonReader.jsonToString();
	    LOG.info("********Getting DF of all the terms********");
	    df = calculator.getDF(documents);
		LOG.info("********Getting All JSON values********");
		jsonValues = jsonReader.getJsonValues();
	}
	
	@Override
	public boolean verifyInformationState(InformationState is) {
		boolean fulfilled = true;
		for(String field:isFields){
			if(is.getISFieldAsString(field)==null){
				fulfilled=false;
				LOG.log(Level.INFO, "Field:"+field+" is needed to complete the task:"+this.name);
			}
		}	
		return fulfilled;
	}


	@Override
	public boolean process(InformationState is) {
		
		if(complete){ //task is complete.
			LOG.log(Level.INFO,"Ending Task");	
		} else {
			complete = true;
			String stemmedQuery = is.getCommonGround().getReference("stemmed");
			String reply;
		    BCJsonReader jsonReader = new BCJsonReader();
			LOG.info("********Calculating Cosine Score table for all Q/A********");
			ArrayList<CosineValueTable> cosineScore = jsonReader.getCosineScore(stemmedQuery, df, jsonValues);
			LOG.info("********Cosine Score table********");
			for(CosineValueTable score: cosineScore) {
	          if (score.getCosineScore()>0)
	          LOG.info(score.toString());;  
	        }
            LOG.info("********Selecting Q/A user intend to ask********");
			reply = jsonReader.selectQuestion(cosineScore);
			System.out.println("Reply in Get Answer is " + reply);
			is.setISField(outField, reply);
           }
      return complete;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
