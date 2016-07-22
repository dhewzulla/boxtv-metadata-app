package uk.co.boxnetwork.mule.components.metadata;


import java.util.List;

import org.mule.api.MuleContext;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Payload;
import org.springframework.beans.factory.annotation.Autowired;


import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.SeriesGroup;


public class ProgrammeComponent {
	@Autowired
	MetadataService metadataService;
	
	public Object process(@Payload String payload, @Lookup MuleContext muleContext) throws Exception{
		List<SeriesGroup> programmes=metadataService.getAllProgrammes();
		return programmes;		
	}
}
