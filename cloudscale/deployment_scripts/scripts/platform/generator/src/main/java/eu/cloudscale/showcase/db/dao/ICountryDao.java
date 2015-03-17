package eu.cloudscale.showcase.db.dao;

import eu.cloudscale.showcase.db.model.ICountry;

public interface ICountryDao extends IDao<ICountry>
{

	public ICountry findById(int id);

	public ICountry getByName(String country);
	
	public void createTable();

}
