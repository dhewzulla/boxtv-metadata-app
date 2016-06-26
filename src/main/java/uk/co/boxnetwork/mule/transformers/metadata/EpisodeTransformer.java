package uk.co.boxnetwork.mule.transformers.metadata;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class EpisodeTransformer extends AbstractMessageTransformer{
		private static final Logger logger=LoggerFactory.getLogger(EpisodeTransformer.class);
		@Autowired
		MetadataService metadataService;
				
		@Override
		public Object transformMessage(MuleMessage message, String outputEncoding)
				throws TransformerException {
			
			String episodeid=MuleRestUtil.getPathPath(message);
			
			if(episodeid==null){			
				List<Episode> episodes=metadataService.getAllEpisodes();
				return episodes;
			}
			else{
				Long id=Long.valueOf(episodeid);
				return metadataService.getAllEpisodeById(id);				
			}
			//return message.getPayload();			
		}
}
