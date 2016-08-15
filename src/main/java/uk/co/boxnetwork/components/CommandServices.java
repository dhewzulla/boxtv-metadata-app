package uk.co.boxnetwork.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.python.jline.internal.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.util.IOUtils;

import uk.co.boxnetwork.data.s3.S3Configuration;

@Service
public class CommandServices {
	private static final Logger logger=LoggerFactory.getLogger(CommandServices.class);
     
	@Autowired
	private S3Configuration s3Configuration;	
	
	public void convertFromMasterImage(String masterImage){
    	 logger.info("converting the master image:"+masterImage);   
    	 try {
    		 convertS3ImageFile(masterImage);
		} catch (Exception e) {
			logger.error("Failed to convert s3 images:masterImage="+masterImage,e);			
		}
    }
     
	
	
	
	
    public void convertS3ImageFile(String masterImage) throws IOException, InterruptedException{
    	String destfilename=masterImage;
    	int ib=destfilename.lastIndexOf("/");
    	if(ib!=1){
    		destfilename=destfilename.substring(ib+1);
    		if(destfilename.length()==0){
    			throw new RuntimeException("filename should not end with slash");
    		}    		
    	}
    	executeCommand("convert_s3_image",s3Configuration.getImageBucket(),masterImage,destfilename, s3Configuration.getImagePublicFolder());
    }
    public String getVideoDuration(String videoURL) throws IOException, InterruptedException{
        videoURL=videoURL.replace("https","http");
    	String outputResult=executeCommand("getvideo_duration",videoURL);
    	if(outputResult!=null){
    		outputResult=outputResult.trim();    		
    	}
    	
    	return outputResult;
    	
    }
    
    public String executeCommand(String scriptname, String... arguments) throws IOException, InterruptedException{
    	List<String> commands=new ArrayList<String>();
    	commands.add("/bin/bash");
    	String userhomedir=System.getProperty("user.home");
    	
    	commands.add(userhomedir+"/bcs3uploader/"+scriptname+".sh");
    	for(String arg:arguments){
    		commands.add(arg);
    	}
    	Map<String, String> env = System.getenv();
    	String pathvalue=env.get("PATH");    	    	
    	
    	ProcessBuilder pb = new ProcessBuilder(commands);
    	Map<String, String> penv = pb.environment();
    	penv.put("PATH",pathvalue+":/usr/local/bin");
    	 
    	Process process = pb.start();    	 
    	int errCode = process.waitFor();    	    	
    	 String outputResult=IOUtils.toString(process.getInputStream());
    	 String errorOutput=IOUtils.toString(process.getErrorStream());
    	 logger.info("**************outputResult=["+outputResult+"]");
    	 logger.info("*************errorOutput=["+errorOutput+"]");
    	 if(errCode!=0){    		 
    		 return "errorCode="+errCode +" with error message:"+errorOutput;
    	 }
    	 else{
    		 return outputResult;
    	 }
    }
    
    

    
    
    
	
}
