package sys.bc;


import java.util.Properties;
import java.util.logging.Logger;
import dm.goals.Goal;
import dm.infostate.InformationState;
import dm.logger.DMLogger;
import dm.nlp.Message;
import dm.tasks.AskQuestionTask;
import dm.utils.textalgs.CalculateTFIDF;


public class BCGoals extends Goal {
	
	protected Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	CalculateTFIDF calculator;
	String reply;

	public BCGoals(String name) {
		super(name);
		LOG.setLevel(DMLogger.DEFAULT_LOG_LEVEL);
    }
	

    @Override
	public void update() {
	
    }

	@Override
	public void init() {
        String cg=InformationState.COMMON_GROUND;
		String conv=InformationState.CONVERSATION_BELIEFS;
	
	   // The tasks are all AskQuestionTask objects.
		Properties greet = new Properties();
		Properties reply = new Properties();
		
		greet.setProperty("question", "ask me something about breast cancer");
		greet.setProperty("inFields", cg+":query"); // these must be <namespace>:<attribute>,<namespace2>:<attrib2>,...
		greet.setProperty("outField",conv+":response");
		reply.setProperty("inFields", cg+":done");
		reply.setProperty("outField", conv+":response");
		
        AskQuestionTask taskGreet = new AskQuestionTask("AskQuestion-Greet", greet);
        GetAnswerTask taskGetQuestion = new GetAnswerTask("GetAnswer", reply);
		
		taskGreet.setIs(this.informationState);
		taskGetQuestion.setIs(this.informationState);
	
		this.addTask(taskGreet);
		this.addTask(taskGetQuestion);
		
     }

	@Override
	public double getConfidence() {
		return 1.0;
	}

	@Override
	public Message createMessage() {
		String pattern = informationState.getISFieldAsString(InformationState.CONVERSATION_BELIEFS+":response");
		
		Message m = null;
		if(pattern!=null && !pattern.isEmpty()){
			m = new Message("");
			m.setProperty("response", pattern);
         }
		
		return m;
	}
	
	

}

