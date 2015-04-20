package eu.cloudscale.showcase.db.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;
import eu.cloudscale.showcase.db.model.mongo.Item;

@Service
public class MongoService extends AService
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
	protected IOrders saveOrder(IAddress address, ICustomer customer,
	        String shipping, IShoppingCart sc, double discount)
	{
		IItemDao itemDao = getItemDaoImpl();
		IOrdersDao ordersDao = getOrdersDaoImpl();
		IOrderLineDao orderLineDao = getOrderLineDaoImpl();
		Calendar cal = Calendar.getInstance();
		Random rand = new Random();

		IOrders order = ordersDao.getObject();
		order.setCustomer( customer );
		order.setOTax( 8.25 );
		order.setODate( new Date() );
		order.setAddressByOBillAddrId( customer.getAddress() );
		order.setAddressByOShipAddrId( address );

		cal.add( Calendar.DATE, rand.nextInt( 7 ) );
		order.setOShipDate( cal.getTime() );
		order.setOShipType( shipping );
		order.setOStatus( "PENDING" );
		order.setOTotal( calculateTotal( sc ) );
		// TODO: order.setOSubTotal( calculateSubTotal(sc) );

		order.setOSubTotal( calculateTotal( sc ) );
		ordersDao.shrani( order );
		
		Set<IShoppingCartLine> res = sc.getShoppingCartLines();
		
		if( res != null && !res.isEmpty() )
		{
    		for ( IShoppingCartLine scl :  res)
    		{
    			IOrderLine ol = orderLineDao.getObject();
    			ol.setItem( scl.getItem() );
    			ol.setOlComment( getRandomString( 20, 100 ) );
    			ol.setOlDiscount( discount );
    			ol.setOlQty( scl.getSclQty() );
    			ol.setOrders( order );
    			order.getOrderLines().add( ol );
    			orderLineDao.shrani( ol );
    			
    			Item item = (Item) itemDao.findById( scl.getItem().getIId() );
    			Integer olQty = item.getOlQty();
    			item.setOlQty( (olQty == null ? 0 : olQty ) + 1 );
    			itemDao.shrani( item );
    
    			// TODO: Update item IStock property
    		}
		}

		ordersDao.shrani( order );

		return order;
	}

	@Override
	@Cacheable("search")
    public List<IItem> searchByTitle(String keyword)
    {
	    IItemDao itemDao = getItemDaoImpl();
	    List<IItem> items = itemDao.findAllByTitle( keyword );
	    
	    return items;
    }

	@Override
	@Cacheable("search")
    public List<IItem> searchBySubject(String keyword)
    {
	    IItemDao itemDao = getItemDaoImpl();
	    List<IItem> items = itemDao.findAllBySubject( keyword );
	    
	    return items;
    }
	
	@Override
	@Cacheable("bestSellers")
	public List<Object[]> getBestSellers(String category)
	{
		IItemDao itemDao = getItemDaoImpl();
	    return itemDao.getBestSellers( category );
	}
}
