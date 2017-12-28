package web.dm.bc;

import dm.dialogue.manager.DM;
import sys.bc.BCAgentLoader;
import sys.bc.BCRules;
import sys.dm.Agent;
import tests.dm.ghd_dialogue.GHDAgentLoader;
import tests.dm.ghd_dialogue.GHDRules;


public class BCAgent2DM extends Agent {

	public BCAgent2DM() {
		super();
	}
	
	@Override
	public void initSystem() {
		// Create new loader
		BCAgentLoader loader = new BCAgentLoader("config from servlet");
		// Here you can set custom variables in the loader if need be
	    dialogue = new DM(loader);
		// Set rules for DM
		// Note: this is for NLU
		dialogue.setRules(new BCRules());
	}

	
	public DM getDialogueManager(){
		return dialogue;
	}
	
	public static void main(String[] args) {
		// Create a new instance of this agent and execute()
		        BCAgent2DM agent = new BCAgent2DM();
				agent.execute();
	}

}
