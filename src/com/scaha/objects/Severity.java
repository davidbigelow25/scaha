package com.scaha.objects;

public class Severity extends ScahaObject {

	//Severity is used for messages
	private String severitycode = null;
	private String severityname = null;
		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setSeveritycode(String value){
		severitycode=value;
	}
	
	public String getSeveritycode(){
		return severitycode;
	}
	public void setSeverityname(String value){
		severityname = value;
	}
	
	public String getSeverityname(){
		return severityname;
	}
	
	
}
