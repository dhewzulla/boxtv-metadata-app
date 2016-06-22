package com.iterativesolution.mule.box.repository;

import javax.persistence.EntityManager;

import org.springframework.transaction.annotation.Transactional;

import com.iterativesolution.mule.box.model.Asset;

public class AssetDAO {
   public EntityManager entityManager;

public EntityManager getEntityManager() {
	return entityManager;
}

public void setEntityManager(EntityManager entityManager) {
	this.entityManager = entityManager;
}
@Transactional 
   public void createAsset(){
		Asset asset=new Asset();
		asset.setId(String.valueOf(System.currentTimeMillis()));
		asset.setName("Dilshat");
		entityManager.persist(asset);

	   
   }
}
