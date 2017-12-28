package sys.bc;

import java.util.ArrayList;
import java.util.Properties;

import dm.filter.InformationStateFilter;
import dm.filter.MessageFilter;
import dm.filter.message.FormatMessageFilter;
import dm.filter.message.StemmerFilter;
import dm.goals.Goal;
import dm.loader.Loader;


public class BCAgentLoader extends Loader {

	public BCAgentLoader(String configFile) {
		super(configFile);
		
	}
	
	/* NLU Filters. Stem Filter and remove punctuation*/
	@Override
	public ArrayList<MessageFilter> loadNluFilters() {
		        // Declare property objects
				Properties stemming = new Properties();
				
				// Set the properties for stemming 
                stemming.setProperty("inputField", "text");
				stemming.setProperty("outputField", "stemmed");

				// Declare an ArrayList of filters
				ArrayList<MessageFilter> filters = new ArrayList<MessageFilter>();
				
				// Add the filters and their properties
				filters.add(new StemmerFilter("stemmer", stemming));
				
				return filters;
    }


	@Override
	public ArrayList<MessageFilter> loadNlgFilters() {
		Properties p = new Properties();
		p.setProperty("inField","response");
		p.setProperty("outField", "text");
		FormatMessageFilter ff= new FormatMessageFilter("Format Filter", p);
        ArrayList<MessageFilter> filters = new ArrayList<MessageFilter>();
		filters .add(ff);
		return filters;

	}

	
	@Override
	public ArrayList<InformationStateFilter> loadISFilters(String key) {
		return new ArrayList<InformationStateFilter>();
	}

	@Override
	public ArrayList<Goal> loadGoals() {
		ArrayList<Goal> g = new ArrayList<Goal>();
		g.add(new BCGoals("Health Chat"));
		return g;
	}

}
