package eu.cloudscale.showcase.db.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.AService;
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
import eu.cloudscale.showcase.db.model.IItem;

@SuppressWarnings( "unchecked" )
@Service
@Transactional(readOnly=true)
public class HibernateService extends AService
{
	@Autowired
	private IAddressDao addressDao;
	
	@Autowired
	private IAuthorDao authorDao;
	
	@Autowired
	private ICcXactsDao ccXactsDao;
	
	@Autowired
	private ICountryDao countryDao;
	
	@Autowired
	private ICustomerDao customerDao;
	
	@Autowired
	private IItemDao itemDao;
	
	@Autowired
	private IOrderLineDao orderLineDao;
	
	@Autowired
	private IOrdersDao ordersDao;
	
	@Autowired
	private IShoppingCartDao shoppingCartDao;
	
	@Autowired
	private IShoppingCartLineDao shoppingCartLineDao;
	
	@Override
	public IAddressDao getAddressDaoImpl()
	{
		return addressDao;
	}

	@Override
	public IAuthorDao getAuthorDaoImpl()
	{
		return authorDao;
	}

	@Override
	public ICcXactsDao getCcXactsDaoImpl()
	{
		return ccXactsDao;
	}

	@Override
	public ICountryDao getCountryDaoImpl()
	{
		return countryDao;
	}

	@Override
	public ICustomerDao getCustomerDaoImpl()
	{
		return customerDao;
	}

	@Override
	public IItemDao getItemDaoImpl()
	{
		return itemDao;
	}

	@Override
	public IOrderLineDao getOrderLineDaoImpl()
	{
		return orderLineDao;
	}

	@Override
	public IOrdersDao getOrdersDaoImpl()
	{
		return ordersDao;
	}

	@Override
	public IShoppingCartLineDao getShoppingCartLineDaoImpl()
	{
		return shoppingCartLineDao;
	}

	@Override
	public IShoppingCartDao getShoppingCartDaoImpl()
	{
		return shoppingCartDao;
	}

	@Override
	@Cacheable("search")
    public List<IItem> searchByTitle(String keyword)
    {
		IItemDao itemDao = getItemDaoImpl();
		List<IItem> items = itemDao.findAllByTitle(keyword);
		
		return items;
    }

	@Override
	@Cacheable("search")
    public List<IItem> searchBySubject(String keyword)
    {
		IItemDao itemDao = getItemDaoImpl();
		List<IItem> items = itemDao.findAllBySubject(keyword);
		
		return items;
    }
	
	@Override
	@Cacheable( "bestSellers" )
	public List<Object[]> getBestSellers(String category)
	{
		IItemDao itemDao = getItemDaoImpl();
		
		List<Object[]> res = itemDao.getBestSellers( category );
		return res;
	}
}
