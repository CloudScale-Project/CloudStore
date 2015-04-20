package eu.cloudscale.showcase.db.dao;

import java.util.List;

import eu.cloudscale.showcase.db.model.IAddress;


public interface IAddressDao extends IDao<IAddress>
{	
	public List<IAddress> findAll();

	public IAddress findById(int id);
	
}
