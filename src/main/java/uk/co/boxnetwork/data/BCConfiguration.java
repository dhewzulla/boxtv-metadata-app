package uk.co.boxnetwork.data;

import org.apache.commons.codec.binary.Base64;

public class BCConfiguration {
	private String oauthurl;
	private String cmsurl;
	private String ingesturl;
	private String accountId;
	private String clientId;
	private String clientSecret;
	
	
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
	public String videoURL(){
		return cmsurl+"/accounts/"+accountId+"/videos";
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
	public String basicAuthentication(){
		String plainCreds = clientId+":"+clientSecret;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		return base64Creds;
		
	}
	public String toString(){
		return "oauthurl=["+oauthurl+"]cmsurl=["+cmsurl+"]ingesturl=["+ingesturl+"]accountId=["+accountId+"]";		
	}
	
  
}
