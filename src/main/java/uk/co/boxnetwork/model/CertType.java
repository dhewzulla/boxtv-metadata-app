package uk.co.boxnetwork.model;

public enum CertType {
   ALL_TIMES("All Times"),
   POST_WATERSHED("Post Watershed");
	private String bcName;	
	CertType(String bcName){
		this.bcName=bcName;
	}
	public String getBcName(){
		return this.bcName;
	}
}
