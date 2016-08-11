package uk.co.boxnetwork.data.bc;

import org.apache.commons.codec.binary.Base64;

public class BCConfiguration {
	private String oauthurl;
	private String cmsurl;
	private String ingesturl;
	private String mediaapiurl;	
	private String accountId;
	private String clientId;
	private String clientSecret;
	private String apiToken;
	
	private String defaltIngestProfile;
	private String ingestCallback;
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getOauthurl() {
		return oauthurl;
	}
	public void setOauthurl(String oauthurl) {
		this.oauthurl = oauthurl;
	}
	public String getCmsurl() {
		return cmsurl;
	}
	public void setCmsurl(String cmsurl) {
		this.cmsurl = cmsurl;
	}
	public String getIngesturl() {
		return ingesturl;
	}
	public void setIngesturl(String ingesturl) {
		this.ingesturl = ingesturl;
	}
	public String requestAccessTokenURL(){
		return oauthurl+"/access_token";		
	}
	public String videoURL(Integer limit, Integer offset, String sort,String q){
		return queryURL(cmsurl+"/accounts/"+accountId+"/videos",limit,offset,sort,q);
	}
	public String queryURL(String baseURL, Integer limit, Integer offset, String sort,String q){
		StringBuilder builder=new StringBuilder();
		builder.append(baseURL);
		if(q!=null||limit!=null||offset!=null||sort!=null){
			builder.append("?");
			boolean appended=false;
			if(limit!=null){
				builder.append("limit="+limit);	
				appended=true;
			}
			if(offset!=null){
				if(appended){
					builder.append("&");
				}
				builder.append("offset="+offset);	
				appended=true;
			}
			if(sort!=null){
				if(appended){
					builder.append("&");
				}
				builder.append("sort="+sort);	
				appended=true;
			}
			if(q!=null){
				if(appended){
					builder.append("&");
				}
				builder.append("q="+q);	
				appended=true;
			}
			
		}
				
		return builder.toString();
	}
	
	public String ingestUrl(String videoid){
		return   ingesturl+"/accounts/"+ accountId+"/videos/"+videoid+"/ingest-requests";		
	}
	public String videoURL(String videoid){
		return cmsurl+"/accounts/"+accountId+"/videos/"+videoid;
	}
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
	
	public String getDefaltIngestProfile() {
		return defaltIngestProfile;
	}
	public void setDefaltIngestProfile(String defaltIngestProfile) {
		this.defaltIngestProfile = defaltIngestProfile;
	}
	public String getIngestCallback() {
		return ingestCallback;
	}
	public void setIngestCallback(String ingestCallback) {
		this.ingestCallback = ingestCallback;
	}
	public String[] retrieveIngestCallbackUrls(){
		if(ingestCallback==null){
			return null;
		}
		return ingestCallback.split(",");
	}
	public String retrieveIngestCallbackUrls(int index){
		String urls[]=retrieveIngestCallbackUrls();
		if(urls==null){
			return null;
		}
		return urls[0];		
	} 
	
	public String basicAuthentication(){
		String plainCreds = clientId+":"+clientSecret;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		return base64Creds;
		
	}
	public String getApiToken() {
		return apiToken;
	}
	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}
	public String getMediaapiurl() {
		return mediaapiurl;
	}
	public void setMediaapiurl(String mediaapiurl) {
		this.mediaapiurl = mediaapiurl;
	}
	
  
}
