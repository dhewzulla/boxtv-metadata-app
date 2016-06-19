package com.iterativesolution.mule.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;



public class XmlToMapParser {
	
	@SuppressWarnings("unchecked")
	private void addToMap(String name,Object content, Map<String,Object> parent){
		if(content==null){
			return;
		}
		else if(content instanceof String && ((String)content).length()==0){
			return;
		}
	    Object obj=	parent.get(name);
	    if(obj==null){
	    	parent.put(name, content);	    	
	    }
	    else if(obj instanceof List){
	    	((List<Object>) obj).add(content);	    	
	    }
	    else {
	    	List<Object> array=new ArrayList<Object>();
	    	array.add(obj);
	    	array.add(content);
	    	parent.put(name, array);
	    }
	}
	public Map<String,Object> toMap(Document document){
		  Element element=document.getRootElement();
		  Map map=new TreeMap<String, Object>();
		  addToMap(element.getName(),toMap(element),map);		  		  
		  return map;	  
	  }
	  
	  private Object toMap(Element element){		  		  
          Map content=new TreeMap();                    
          for ( Iterator<Attribute> i = element.attributeIterator(); i.hasNext(); ) {
        	  Attribute at=(Attribute)i.next();
        	 if(at.getName().startsWith("xmlns")||at.getName().startsWith("type")){
        	   continue;        	   
        	 }
        	 content.put(at.getName(), at.getValue());        	  
          }
          
	      for ( Iterator<Element> i = element.elementIterator(); i.hasNext(); ) {
	          Element elem = (Element) i.next();
	          addToMap(elem.getName(),toMap(elem),content);
	      }
	      if(content.isEmpty()){
	    	  String text=element.getText();
	    	  if(text!=null && text.length()>0){
	    		  return text;
	    	  }
	    	  else
	    		  return null;
	      }
	      return content;		  
	  }

}
