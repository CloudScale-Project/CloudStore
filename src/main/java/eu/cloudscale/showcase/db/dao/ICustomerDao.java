package eu.cloudscale.showcase.db.dao;

import java.util.List;

import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICustomer;


public interface ICustomerDao extends IDao<ICustomer> 
{

	public ICustomer findById(Integer id);

	public ICustomer getUserBy(String username, String password);

	public List<ICustomer> findByAddress(IAddress addrId);

	

}
