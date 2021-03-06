package uk.co.boxnetwork.data;

import java.util.List;

import javax.persistence.TypedQuery;

import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Series;

public class SearchParam {
	static final protected Logger logger=LoggerFactory.getLogger(SearchParam.class);
	private String search=null;
	private Integer start=null;
	private Integer limit=null;
	private String contractNumber=null;
	private String title=null;
	
	
	
	public SearchParam(String search, Integer start, Integer limit) {
		super();
		this.search = search;
		this.start = start;
		this.limit = limit;
	}
	public SearchParam(MuleMessage message, AppConfig appConfig){
		this.limit=appConfig.getRecordLimit();		
		ParameterMap queryparams=message.getInboundProperty("http.query.params");
		if(queryparams!=null){				
				this.search=queryparams.get("search");		
				if(this.search!=null){
						this.search=this.search.trim();
						if(this.search.length()==0){
							this.search=null;
						}
						else{
							if(search.indexOf("%")==-1){
								search="%"+search+"%";				
							}
						}
				}
				this.contractNumber=queryparams.get("contractNumber");
				String startParam=queryparams.get("start");
				if(startParam!=null){
					startParam=startParam.trim();
					if(startParam.length()>0){
						try{
				    		this.start=Integer.valueOf(queryparams.get("start"));
				    	}
				    	catch(Exception e){
				    		logger.error(e+ " while convering the startParam:"+startParam,e);
				    	}
					}
				}
				this.title=queryparams.get("title");
				if(this.title!=null){
					this.title=this.title.trim();
					if(this.title.length()==0){
						this.title=null;
					}
				}
		}
		
		
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}	

	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	public String selectQuery(String allquery,String  filterQuery){				   
		   if(this.search==null){
			   return allquery;
		   }
		   else{			    
			    return filterQuery;
		   }
   }
	
	public String selectQuery(String allquery,String  filterQuery, String titleQuery){				   
		if(this.title!=null){
			return titleQuery;
		}
		else if(this.search==null){
			   return allquery;
		 }		   
		   else{			    
			    return filterQuery;
		   }
    }
	

	
	public String selectSeriesQuery(String allquery,String  filterQuery, String contractQuery){				   
			if(this.contractNumber!=null){
				return contractQuery;
			}
			else if(this.search==null){
			   return allquery;
		   }
		   else{			    
			    return filterQuery;
		   }
  }
	public void config(TypedQuery<?> typedQuery){
		if(this.title!=null){
			typedQuery.setParameter("title",this.title);
		}
		else if(this.contractNumber!=null){
			typedQuery.setParameter("contractNumber",this.contractNumber);
		}
		else if(this.search!=null){
			typedQuery.setParameter("search",this.search);
		   }
		
		if(this.start!=null && this.start>0){
			  typedQuery.setFirstResult(this.start);			   
		   }
		   if(this.limit!=null && this.limit>=0){
			   typedQuery.setMaxResults(this.limit);
		   }		   
	}
   public boolean isEnd(int fetchSize){
	   return fetchSize<limit;
   }
   public void nextBatch(){
	   this.start+=this.limit;
   }
   public void nextBatch(int videoSize){
	   this.start+=videoSize;
   }

}
