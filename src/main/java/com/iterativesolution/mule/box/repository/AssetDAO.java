package com.iterativesolution.mule.box.repository;

import javax.persistence.EntityManager;

import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.model.Asset;

public class AssetDAO {
   public EntityManager entityManager;

public EntityManager getEntityManager() {
	return entityManager;
}

public void setEntityManager(EntityManager entityManager) {
	this.entityManager = entityManager;
}
   @Transactional 
   public void createAsset(Asset asset){		
		entityManager.persist(asset);
   }
   public Asset getAssetById(String id){
	   return entityManager.find(Asset.class, id);
   }
   
  
}
