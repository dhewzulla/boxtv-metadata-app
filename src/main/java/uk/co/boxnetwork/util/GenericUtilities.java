package uk.co.boxnetwork.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.model.AppConfig;
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
  
  public static String materialIdToVideoFileName(String materialID){
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
		  int ie=fpath.indexOf(".");
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
  public static boolean transcodeConditionNotStasfied(VideoStatus status){
  	return status==VideoStatus.MISSING_PROFILE || status==VideoStatus.MISSING_VIDEO || status == VideoStatus.NO_PLACEHOLDER;
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
	   else if(episode.getEpisodeStatus()==null || episode.getEpisodeStatus().getVideoStatus()==null || transcodeConditionNotStasfied(episode.getEpisodeStatus().getVideoStatus())){		   
		   return VideoStatus.NEEDS_TRANSCODE;
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
  public static String toWebsafeTitle(String title){
	  if(title==null||title.length()==0){
		  return title;
	  }
	  return   title.toLowerCase().replaceAll("[&\\/\\\\#,\\ +()$~%.'\":*?<>{}]","-");	  	  
  }
  public static String fromWebsafeTitle(String websafeTitle){
	  if(websafeTitle==null||websafeTitle.length()==0){
		  return websafeTitle;
	  }	  
	  return websafeTitle.replace("-", "_");	  
  }
  public static String partsToMatId(String [] parts, int first){
	  if(first>=parts.length){
		  return null;
	  }
	  StringBuilder builder=new StringBuilder();
	  builder.append(parts[first]);	  
	  for(int i=first+1;i<parts.length;i++){
		  builder.append("/");
		  builder.append(parts[i]);
	  }
	  return builder.toString();
  }
  public static String materialIdToImageFileName(String materialId){
	  if(materialId==null){
		  return null;
	  }
	  else{
		  return materialId.replace("/", "_");		  
	  }
  }
  public static String fixChannel(String channelName){
	  if("Box Hits (SmashHits)".equals(channelName)){		  
		  return "Box Hits";
	  }
	  else if("Box Upfront (Heat)".equals(channelName)){
		  return "Box Upfront";
	  }
	 return channelName;
  }
  public static String[] commandDelimitedToArray(String tags){
	  if(tags==null){
			return null;			
	  }
	  tags=tags.trim();
	  if(tags.length()==0){
		  return null;
	  }
	  String[] tagArray=tags.split(",");			
	  for(int i=0;i<tagArray.length;i++){
		  	tagArray[i]=tagArray[i].trim();						
	  }
	  return tagArray;
  }
  
  public static String arrayToCommaSeparated(String tags[]){
		if(tags==null ||tags.length==0){
			return null;	
		}
		else{
				String v=tags[0].trim();
				List<String> added=new ArrayList<String>();
				added.add(v);			
				for(int i=1;i<tags.length;i++){
					 tags[i]=tags[i].trim();
					 if(added.contains(tags[i])){
						 	continue;					
					 }
					 v=v+", ";
					 v=v+tags[i];
					 added.add(v);
				}
				return v;
		}
  
  }
	
  
  

  public static Date nextYearDate(){
	    Calendar calendar=Calendar.getInstance();		
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		return calendar.getTime();
  }
	public static String getImageWithSize(AppConfig config, String imagename, int width, int height){
		String basename=imagename;
		String ext="";
		int ib=imagename.indexOf(".");
						
		if(ib!=-1){
			 basename=imagename.substring(0,ib);
			 ext=imagename.substring(ib+1);
		}
		String template=config.getImagetemplateurl();
		String imgURL=template.replace("{image_name}",basename);
		imgURL=imgURL.replace("{width}", String.valueOf(width));
		imgURL=imgURL.replace("{height}", String.valueOf(height));
		imgURL=imgURL.replace("{ext}", ext);
		return imgURL;
	}
		
	
}

