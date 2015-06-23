package eu.cloudscale.showcase.db;

import eu.cloudscale.showcase.db.dao.IAddressDao;
import eu.cloudscale.showcase.db.dao.IAuthorDao;
import eu.cloudscale.showcase.db.dao.ICcXactsDao;
import eu.cloudscale.showcase.db.dao.ICountryDao;
import eu.cloudscale.showcase.db.dao.ICustomerDao;
import eu.cloudscale.showcase.db.dao.IItemDao;
import eu.cloudscale.showcase.db.dao.IOrderLineDao;
import eu.cloudscale.showcase.db.dao.IOrdersDao;
import eu.cloudscale.showcase.db.dao.IShoppingCartDao;
import eu.cloudscale.showcase.db.dao.IShoppingCartLineDao;


public interface IDaos
{
	public IAddressDao getAddressDaoImpl();
	
	public IAuthorDao getAuthorDaoImpl();
	
	public ICcXactsDao getCcXactsDaoImpl();
	
	public ICountryDao getCountryDaoImpl();
	
	public ICustomerDao getCustomerDaoImpl();
	
	public IItemDao getItemDaoImpl();

	public IOrderLineDao getOrderLineDaoImpl();
	
	public IOrdersDao getOrdersDaoImpl();
	
	public IShoppingCartLineDao getShoppingCartLineDaoImpl();

	public IShoppingCartDao getShoppingCartDaoImpl();
	 
}
