package uk.co.boxnetwork.components;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.model.Asset;


@Repository
public class BoxMedataRepository {
	
      @Autowired	
	  private EntityManager entityManager;

	   @Transactional 
	   public void createAsset(Asset asset){		
			entityManager.persist(asset);
	   }
	   @Transactional
	   public Asset getAssetById(String id){
		   return entityManager.find(Asset.class, id);
	   }

}
