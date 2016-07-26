package uk.co.boxnetwork.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.model.VideoStatus;
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
  public static boolean isNotValidName(String v){
	  return isNotValidTitle(v);
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
  
  public static String getValueInMap(Map<String, Object> messageMap, String paths){
	  String[] path=paths.split("\\.");
	  return getValueInMap(messageMap,path);
	  
  }
  public static String getValueInMap(Map<String, Object> messageMap, String path[]){
	  return getValueInMap(messageMap,path,0);
  }
  private static Object getValueInMapByVar(Map<String, Object> messageMap, String variableName){
	  
    int ib=variableName.indexOf("[");
    int ie=variableName.indexOf("]");
    if(ib!=-1 && ie>ib){
    	String indexString=variableName.substring(ib+1,ie);
    	String varName=variableName.substring(0,ib);
    	int ind=Integer.valueOf(indexString);
    	Object obj=messageMap.get(varName);
    	if(obj instanceof ArrayList){
    		List<Object> objarray=(List<Object>)obj;
    		if(objarray.size()<=ind){
    			logger.warn(" array index out of boundary in getValueInMapByVar() varName=["+varName+"]ind=["+ind+"]but array size is:"+objarray.size());
    			return null;
    		}
    		else
    			return objarray.get(ind);
    	}
    	return obj;
    	
    }
    else{
    	return messageMap.get(variableName);
    }
	  
	  
	  
  }
  private  static String getValueInMap(Map<String, Object> messageMap, String path[], int index){
	  if(index>=path.length){
		  return null;
		  
	  }
	  
	  Object obj=getValueInMapByVar(messageMap,path[index]);
	  if(obj==null){
		  return null;
	  }
	  if((index+1)>=path.length){
		  if(obj instanceof String){			  
			  return (String)obj;
		  }
		  else{
			  logger.warn("Unpexected type on "+path+":"+index+":"+obj.getClass().getName());
			  return null;			  
		  }
	  }
	  else if(obj instanceof Map){
		  Map<String, Object> objMap=(Map<String, Object>)obj;
		  return getValueInMap(objMap, path,index+1);
	  }
	  else{
		  logger.warn("Unpexected type on "+path+":"+index+":"+obj.getClass().getName());
		  return null;
		  
	  }
	  
  }
  
  public static String materialIdToFileName(String materialID){
	  String matParts[]=materialID.split("/");
		 StringBuilder filenameBuilder=new StringBuilder();
		 filenameBuilder.append("V");
		 int counter=0;
		 for(String mpart:matParts){
			 filenameBuilder.append("_");
			 filenameBuilder.append(mpart);
			 counter++;
			 if(counter>2){
				 break;
			 }
	     }
		 return filenameBuilder.toString();
  }
  public static String fileNameToMaterialID(String filename){
	  if(filename.startsWith("V_") || filename.startsWith("v_")){
		  String fpath=filename.substring(2);
		  int ie=filename.indexOf(".");
		  if(ie!=-1){
			  fpath=fpath.substring(0,ie);
		  }
		  String matParts[]=fpath.split("_");
		  StringBuilder matidBuilder=new StringBuilder();			 
			 int counter=0;
			 for(String mpart:matParts){
				 if(counter>0){
					 matidBuilder.append("/");
				 }				 
				 matidBuilder.append(mpart);
				 counter++;
				 if(counter>1){
					 break;
				 }
		     }
			 return matidBuilder.toString();
	  }		
	  else
		  return null;
  }
  
  public static String getProgrammeNumber(uk.co.boxnetwork.data.Episode episode){
	  
	  if(isNotValidCrid(episode.getProgrammeNumber())){
		  String materialId=episode.getMaterialId();
		  String matParts[]=materialId.split("/");
		  if(matParts.length<=2){
			  return materialId;
		  }
		  else{
			  return matParts[0]+"/"+matParts[1];
		  }
	  }
	  else
		  return episode.getProgrammeNumber();
	  
	  
  }
  public static String getContractNumber(uk.co.boxnetwork.data.Episode episode){
	  String pid=episode.getProgrammeNumber();
	  if(isNotValidCrid(pid)){
		  pid=episode.getMaterialId();
	  }
	  if(isNotValidCrid(pid)){
		  return null;
	  }
	  String matParts[]=pid.split("/");
	  return matParts[0];
  }
  public static VideoStatus calculateVideoStatus(Episode episode){
	   return  calculateVideoStatus(episode, episode.getIngestSource(), episode.getIngestProfile());  
  }
  public static VideoStatus calculateVideoStatus(Episode episode, String previousIngestSource, String previousIngestProfile){
	   if(isEmpty(episode.getIngestSource())){
			return VideoStatus.MISSING_VIDEO;		
	   }
	   else if(isEmpty(episode.getIngestProfile())){
			return VideoStatus.MISSING_PROFILE;
	   }
	   else if(isEmpty(episode.getBrightcoveId())){
			return VideoStatus.NO_PLACEHOLDER;
	   }
	   else if((!episode.getIngestSource().equals(previousIngestSource)) || (!episode.getIngestProfile().equals(previousIngestProfile))){
		    return VideoStatus.NEEDS_RETRANSCODE;
	   }
	   else{
		   return null;
	   }
		   	   
 }
  public static MetadataStatus calculateMetadataStatus(Episode episode){
	  if(isEmpty(episode.getBrightcoveId())){
			return MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER;			
	   }
	  else
		    return null; 
  }
  public static String validateEpisode(uk.co.boxnetwork.data.Episode episode){
		if(isNotValidCrid(episode.getProgrammeNumber())){
			   return "programNumber is not valid";
		}
		if(isNotValidTitle(episode.getTitle())){
			   return "title is not valid";
		}
		if(isNotValidCrid(episode.getMaterialId())){
			episode.setMaterialId(episode.getProgrammeNumber());
		}
		return null;		
	}
}
