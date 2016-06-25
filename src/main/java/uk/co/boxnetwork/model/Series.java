package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.co.boxnetwork.util.GenericUtilities;

@Entity(name="series")
public class Series {
	@Id
	@GeneratedValue
    private Long id;
	
	@Column(name="asset_id")
	private String assetId;
	
	@Column(name="primary_id")
	private String primaryId;
	
	private String name;

	@Column(name="contract_number")
	private String contractNumber;
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "programme_id", nullable = true )
	private Programme programme;
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getPrimaryId() {
		return primaryId;
	}

	public void setPrimaryId(String primaryId) {
		this.primaryId = primaryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	
	public Programme getProgramme() {
		return programme;
	}

	public void setProgramme(Programme programme) {
		this.programme = programme;
	}

	public void merge(Series series){
		if(GenericUtilities.isNotAValidId(this.assetId)){
			this.assetId=series.getAssetId();			
		}
		if(GenericUtilities.isNotAValidId(this.primaryId)){
			this.primaryId=series.getPrimaryId();			
		}
		if(GenericUtilities.isNotValidTitle(this.name)){
			this.name=series.getName();			
		}
		if(GenericUtilities.isNotValidContractNumber(this.contractNumber)){
			this.contractNumber=series.getContractNumber();			
		}
		if(this.programme==null){
			this.programme=series.getProgramme();
		}
	}
	

}
