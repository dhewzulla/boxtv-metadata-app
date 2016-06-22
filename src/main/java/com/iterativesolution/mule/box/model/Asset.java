package com.iterativesolution.mule.box.model;



import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;


@Entity(name="asset")
@NamedQuery(name = "asset.findAssetByName", query = "from asset a where a.name = ?1")

public class Asset {

@Id
private String id;

private String name;

private  String type;

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}


}
