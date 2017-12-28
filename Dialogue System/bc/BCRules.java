package sys.bc;

import sys.dm.DummyRules;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dm.infostate.InformationState;
import dm.infostate.grounding.Fact;
import dm.logger.DMLogger;
import dm.nlp.Message;

public class BCRules extends DummyRules {
	protected Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	

	public BCRules() {
		// TODO Auto-generated constructor stub
		LOG.setLevel(DMLogger.DEFAULT_LOG_LEVEL);
	}
	
   // What to do when a message arrives here.
   @Override
	public void process(Message message) {
			// This simply copies fields into the CommonGround object in the info state.
			for(String key:message.getProperties().stringPropertyNames()){
				this.infoState.getCommonGround().ground(new Fact(key, message.getProperty(key)));
			}
			isDone(message.getMessageText());
            findQuestion(message.getMessageText());
          }
		
		
   public void findQuestion(String text){
			String cg=InformationState.COMMON_GROUND;
			LOG.info("inside findquestion for " + text);
            this.infoState.getCommonGround().ground(new Fact("query","yes"));
        }
		
   public void isDone(String text){
			String cg=InformationState.COMMON_GROUND;
			LOG.info("infoState.getISFieldAsString query" +infoState.getISFieldAsString(cg+":query") );
            if (infoState.getISFieldAsString(cg+":query")!=null)
            {
              Pattern pattern = Pattern.compile("bye|goodbye thanks|thanks|thank you|that will be all|that would be all");
    		  Matcher m = pattern.matcher(text.toLowerCase());
    		  if (m.matches())
            			this.infoState.getCommonGround().ground(new Fact("done","yes"));
            }
          }
   }

