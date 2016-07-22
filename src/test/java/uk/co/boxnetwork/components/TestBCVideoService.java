package uk.co.boxnetwork.components;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import static org.junit.Assert.*;

import com.amazonaws.util.IOUtils;

import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.mule.components.LoadResourceAsInputStream;


public class TestBCVideoService {

	@Test
	public void jsonToBCVideoDataShouldReturnBCVideoData() throws IOException{
		InputStream ins=LoadResourceAsInputStream.class.getClassLoader().getResourceAsStream("data/bc/bc-video.json");
		String jsonText=IOUtils.toString(ins);
		System.out.println(jsonText);
		BCVideoService videoService=new BCVideoService();
		BCVideoData videoData=videoService.jsonToBCVideoData(jsonText);
		assertEquals("drm ", "True", videoData.getCustom_fields().getDrm());
	}
}