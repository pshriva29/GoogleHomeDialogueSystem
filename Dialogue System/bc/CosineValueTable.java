/*
 * Author : Pragati Shrivastava
 * Date : July 2017
 * Description : Class to hold objects of Cosine Score table. These objects are used in the arraylist in the CalculateTFIDF.java class
 * 
 * */


package sys.bc;

public class CosineValueTable {
	
	public Long id;
	public String qa;
	public String text;
	public double cosineScore;
	
	public CosineValueTable(Long id, String qa, String text, double cosineScore) {
		super();
		this.id = id;
		this.qa = qa;
		this.text = text;
		this.cosineScore = cosineScore;
	}

	public CosineValueTable() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQa() {
		return qa;
	}

	public void setQa(String qa) {
		this.qa = qa;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getCosineScore() {
		return cosineScore;
	}

	public void setCosineScore(double cosineScore) {
		this.cosineScore = cosineScore;
	}
	
	@Override
	   public String toString() {
	        return ("ID:"+this.getId()+
	                    " Q/A "+ this.getQa()+
	                    " Text: "+ this.getText()+
	                    " Cosine : " + this.getCosineScore());
	   }

}
