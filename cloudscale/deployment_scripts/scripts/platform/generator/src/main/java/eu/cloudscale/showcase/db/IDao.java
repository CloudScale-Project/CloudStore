package eu.cloudscale.showcase.db;

import eu.cloudscale.showcase.db.dao.IAddressDao;
import eu.cloudscale.showcase.db.dao.ICustomerDao;
import eu.cloudscale.showcase.db.dao.IItemDao;
import eu.cloudscale.showcase.db.dao.IShoppingCartDao;
import eu.cloudscale.showcase.db.dao.IShoppingCartLineDao;


public interface IDao
{
	
	public IItemDao getItemDao();

	public IShoppingCartLineDao getShoppingCartLineDao();

	public IShoppingCartDao getShoppingCartDao();

	public IAddressDao getAddressDao();

	public ICustomerDao getCustomerDao();

	public IShoppingCartLineDao getShoppingCartLineDaoImpl();
	 
}
