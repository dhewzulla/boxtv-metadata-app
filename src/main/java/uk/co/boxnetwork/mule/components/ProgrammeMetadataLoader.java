package uk.co.boxnetwork.mule.components;

import java.util.ArrayList;
import java.util.List;

import org.mule.api.MuleContext;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Payload;
import org.springframework.beans.factory.annotation.Autowired;


import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.Programme;


public class ProgrammeMetadataLoader {
	@Autowired
	MetadataService metadataService;
	
	public Object process(@Payload String payload, @Lookup MuleContext muleContext) throws Exception{
		List<Programme> programmes=metadataService.getAllProgrammes();
		return programmes;		
	}
}
