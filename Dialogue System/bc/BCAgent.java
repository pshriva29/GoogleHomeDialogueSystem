package sys.bc;

import sys.dm.Agent;
import dm.dialogue.manager.DM;

public class BCAgent extends Agent {

	public BCAgent() {
		super();
	}

	public void initSystem() {
		BCAgentLoader bcdl = new BCAgentLoader("");
		dialogue = new DM(bcdl);
		dialogue.setRules(new BCRules());
	}

	public static void main(String[] args) {
		BCAgent bcda = new BCAgent();
		System.out.println("Start");
		bcda.execute();
	}

}
