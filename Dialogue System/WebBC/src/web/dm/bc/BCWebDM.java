package web.dm.bc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

import dm.dialogue.manager.DM;
import dm.nlp.Message;

/**
 * S ervlet implementation class WebBC
 */
@WebServlet("/BCWebDM")
public class BCWebDM extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BCAgent2DM dm;
	private boolean first; // Moving inside method
	private DM dialogue; // Created reference of DM
	
	 HashMap<String, BCAgent2DM> sessionIDs = new HashMap<String, BCAgent2DM>();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BCWebDM() { 
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
	    PrintWriter res = response.getWriter();
        String convid = request.getParameter("convid"); // Get conversation id from google home
		String ssnKey;
		JSONObject respJsonObj = new JSONObject();
	
        if(!(sessionIDs.containsKey(convid)))
			{
				dm = new BCAgent2DM();
			    first = true;
				sessionIDs.put(convid, dm);
			}

		Set<Entry<String, BCAgent2DM>> ssnIdEntrySet = sessionIDs.entrySet();
		Iterator<Entry<String, BCAgent2DM>> ssnIdEntrySetIterator = ssnIdEntrySet.iterator();
        while(ssnIdEntrySetIterator.hasNext())
		{
			Entry<String, BCAgent2DM> ssnIdEntry = ssnIdEntrySetIterator.next();
			ssnKey = ssnIdEntry.getKey();
			if(ssnKey.equals(convid))
			{
				dialogue = ssnIdEntry.getValue().getDialogueManager();
			}
		}
		
		String msg = request.getParameter("msg");
		String resp = process(msg,convid,dialogue); //Added session id to check
		System.out.println("In:"+msg+", out:"+resp);
		if(resp == null)   // If User ends the conversation and null response returned
		{
			resp = "Goodbye.Take care";
			respJsonObj.put("answer", resp);
			sessionIDs.remove(convid);
		}
       else if(resp.contains(".mp3"))  // If User request for story and audio is returned as response
        {
        	respJsonObj.put("audio", resp); 
        }
       else   // If user asks a question and answer is returned as response
       {
    	   respJsonObj.put("answer", resp);
       }
	
		res.write(respJsonObj.toString());
		res.flush();
		res.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	protected String process(String userText, String ssnId, DM dialogue2){
		String text = "";
		System.out.println("first?"+first);
		if(first)
		{
			if(userText == null)
			{
				text = dialogue.takeTurn(null);
			}
			else
			{
				Message firstMsg = new Message(userText);
				text = dialogue.takeTurn(firstMsg);
			}
			
		}
		else if (!dialogue.isOver()){
			Message msg = new Message(userText);
			text = dialogue.takeTurn(msg);
		}
		first = false;
		return text;
	}
}

