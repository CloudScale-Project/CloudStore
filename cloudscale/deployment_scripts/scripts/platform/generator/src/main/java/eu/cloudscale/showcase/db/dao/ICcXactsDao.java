package eu.cloudscale.showcase.db.dao;

import eu.cloudscale.showcase.db.model.ICcXacts;

public interface ICcXactsDao extends IDao<ICcXacts> 
{

	int getLastCcXactsId();

	public ICcXacts findById(Integer id);
}
