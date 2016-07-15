package uk.co.boxnetwork.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;

import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.mule.components.LoadResourceAsInputStream;

public class GenericUtilities {
	private static final Logger logger=LoggerFactory.getLogger(GenericUtilities.class);
	
	
	static {
	    try {
	        Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
	        field.setAccessible(true);
	        field.set(null, java.lang.Boolean.FALSE);
	    } catch (Exception ex) {
	    }
	} 
	
	public static StrongTextEncryptor getEncryptor(String encruptionKey){
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
 	    textEncryptor.setPassword(encruptionKey);
 	    return textEncryptor;
 	   
	}
	public static String encrypt(String encruptionKey,String content){
		StrongTextEncryptor textEncryptor =getEncryptor(encruptionKey); 
		return textEncryptor.encrypt(content);		 	   
	}
	public static String decrypt(String encruptionKey,String content){
		StrongTextEncryptor textEncryptor =getEncryptor(encruptionKey); 
		return textEncryptor.decrypt(content);		 	   
	}
	
  public static boolean equalString(String v1, String v2){
	  if(v1==null){
		   return v2==null;		   
	  }
	  else 
		  return v1.equals(v2);
  }
  public static boolean isNotValidCrid(String v){
	  if(isNotAValidId(v)){
		  return true;
	  }
	  v=v.trim();
	  if(v.length()<5){
		  return true;		  
	  }
	  return false;
  }
  public static boolean isNotValidContractNumber(String v){
	  if(isNotAValidId(v)){
		  return true;
	  }
	  v=v.trim();
	  if(v.length()<5){
		  return true;		  
	  }
	  return false;
  }
  public static boolean isNotValidTitle(String v){
	  if(isNotAValidId(v)){
		  return true;
	  }
	  v=v.trim();
	  if(v.length()<3){
		  return true;		  
	  }
	  return false;
  }
  public static boolean isNotAValidId(String v){
	  if(v==null){
		  return true;
	  }
	 if(v.length()==0)
		 return true;
	 v=v.trim();
	 if(v.length()==0)
		 return true;
	 if(v.equals("0")){
		 return true;
	 }
	 return false;	 
  }
  public static String readStream(InputStream is) {
	    StringBuilder sb = new StringBuilder(512);
	    try {
	        Reader r = new InputStreamReader(is, "UTF-8");
	        int c = 0;
	        while ((c = r.read()) != -1) {
	            sb.append((char) c);
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	    return sb.toString();
	}
  public static String readFileContent(String file){
	  InputStream in=LoadResourceAsInputStream.class.getClassLoader().getResourceAsStream(file);
	  return readStream(in);
	  
  }
  public static boolean isEmpty(String v){
	  if(v==null){
		  return true;
	  }
	  v=v.trim();
	  return v.length()==0;
  }
  public static Integer bcInteger(String v){
	  if(isEmpty(v)){
		  return null;
	  }
	  try{
		  return Integer.valueOf(v);
	  }
	  catch(Exception e){
		  logger.error(e+ " converting to integer:"+v,e);
		  return null;
	  }
	  
  }
  public static String bcString(String v){
	  if(isEmpty(v)){
		  return null;
	  }
	  return v;
	  
  }
  
}
