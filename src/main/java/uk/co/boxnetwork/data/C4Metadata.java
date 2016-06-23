package uk.co.boxnetwork.data;

import java.util.ArrayList;
import java.util.List;

import uk.co.boxnetwork.model.Asset;

public class C4Metadata {
  private List<Asset> assets=new ArrayList<Asset>();
  public List<Asset> getAssets() {
	return assets;
  }
   public void addAsset(Asset asset){
	  assets.add(asset);	  
  }
   
}
