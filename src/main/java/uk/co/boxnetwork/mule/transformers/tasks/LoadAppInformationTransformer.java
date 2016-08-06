package uk.co.boxnetwork.mule.transformers.tasks;

import java.util.List;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.TimedTaskService;
import uk.co.boxnetwork.data.AppConfig;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.app.AppInfo;
import uk.co.boxnetwork.model.TimedTask;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class LoadAppInformationTransformer extends BoxRestTransformer{
	
	@Autowired
	private AppConfig appConfig;
	
	@Override
	 protected Object processGET(MuleMessage message, String outputEncoding){
		AppInfo appInfo=new AppInfo();
		appInfo.setAppconfig(appConfig);
			return appInfo;
	 }
   
}