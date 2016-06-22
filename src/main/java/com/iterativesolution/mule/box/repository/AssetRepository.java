package com.iterativesolution.mule.box.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iterativesolution.mule.box.model.Asset;



public interface AssetRepository  extends CrudRepository<Asset, Long> {
	
	 Asset save(Asset entity);
	 
	 
	Asset findAssetById(String username);
	long count(); 
	void delete(Asset asset);
}
