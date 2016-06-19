package com.iterativesolution.mule.transformers;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;

import com.iterativesolution.mule.payload.MultipleJsonInputStream;
import com.iterativesolution.mule.payload.MultiplePayloadsInputStream;


public class JoinMultipleJsonStreamTransformer extends CopyOnWriteArrayListTransformer{
	@Override
	public Object createMultiplePayloadsInputStream(CopyOnWriteArrayList payload,String outputEncoding,MuleMessage message) throws TransformerException{
		return new MultipleJsonInputStream(getInputStreamListFromCopyOnWriteArrayList(payload,outputEncoding));		
	}
	
}
