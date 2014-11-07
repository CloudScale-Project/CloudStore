package eu.cloudscale.showcase.db.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.BuyConfirmResult;
import eu.cloudscale.showcase.db.common.ContextHelper;
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
import eu.cloudscale.showcase.db.dao.mongo.impl.ShoppingCartDaoImpl;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.ICcXacts;
import eu.cloudscale.showcase.db.model.ICountry;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;
import eu.cloudscale.showcase.db.model.hibernate.ShoppingCart;

@Transactional(readOnly=true)
public abstract class AService implements IService
{	
	@Autowired
    protected IAddressDao addressDao;
	
	@Autowired
	protected IAuthorDao authorDao;
	
	@Autowired
	protected ICcXactsDao ccXactsDao;
	
	@Autowired
	protected ICountryDao countryDao;
	
	@Autowired
	protected ICustomerDao customerDao;
	
	@Autowired
    protected IItemDao itemDao;
	
	@Autowired
    protected IOrderLineDao orderLineDao;
	
	@Autowired
    protected IOrdersDao ordersDao;
	
	@Autowired
    protected IShoppingCartDao shoppingCartDao;
	
	@Autowired
	protected IShoppingCartLineDao shoppingCartLineDao;
	
	@Override
	@Transactional(readOnly=false)
    public IShoppingCart createEmptyCart()
    {
		IShoppingCartDao scDao = shoppingCartDao;
		IShoppingCart sc = new ShoppingCart();
		sc.setScTime( new Date() );
		//getCurrentSession().saveOrUpdate(sc);
		scDao.shrani( sc );
		
		return sc;
    }
	
	@SuppressWarnings( "rawtypes" )
	@Override
	@Cacheable("newProducts")
	public List getNewProducts(String category)
	{
		System.out.println("getNewProducts(" + category + ") called!");
		return itemDao.getNewProducts( category );
	}
	
	@Override
	@Transactional(readOnly=false)
	public IShoppingCart doCart(IShoppingCart sc, Integer itemId,
	        List<Integer> ids, List<Integer> quantities)
	{

		IShoppingCartLineDao sclDao = shoppingCartLineDao;
		IShoppingCartDao scDao = shoppingCartDao;

		if ( itemId != null )
		{
			addItem( sc, itemDao.findById( itemId.intValue() ) );
		}

		refreshCart( sc, ids, quantities );
		addRandomItemToCartIfNecessary( sclDao, sc );
		resetCartTime( sclDao, sc );

		return sc;
	}
	
	protected void resetCartTime(IShoppingCartLineDao sclDao, IShoppingCart sc)
	{
		try 
		{
			IShoppingCartDao scDao = shoppingCartDao;
			sc.setScTime( new Date() );
  			scDao.shrani( sc );
		}
		catch(Exception e)
		{
			System.out.println("ShoppingId = " + sc.getScId());
			e.printStackTrace();
		}
	}
	
//	@Transactional(readOnly=false)
	protected void addRandomItemToCartIfNecessary(IShoppingCartLineDao sclDao,
	        IShoppingCart sc)
	{
		// check and see if the cart is empty. If it's not, we do
		// nothing.

		try
		{
			// Check to see if the cart is empty
			if ( sc.getShoppingCartLines().size() == 0 )
			{
				// Cart is empty
				addItem( sc, itemDao.getRandomItem() );
			}

		}
		catch ( java.lang.Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	protected IShoppingCart getCart(Integer shoppingId, double discount)
	{
		IShoppingCartDao scDao = shoppingCartDao;
		IShoppingCart sc = scDao.findById( shoppingId );
		return sc;
	}

	// protected void addRandomItemToCartIfNecessary(
	// IShoppingCartLineDao sclDao, Integer shoppingId)
	// {
	// IItemDao itemDao = getItemDaoImpl();
	//
	// if ( sclDao.isCartEmpty( shoppingId ) )
	// {
	// IItem randItem = itemDao.getRandomItem();
	// // related_item = getRelated1(sclDao, shoppingId, randItem);
	// addItem( shoppingId, randItem );
	// }
	//
	// }

	// protected IItem getRelated1(IShoppingCartLineDao<IShoppingCartLine>
	// sclDao,
	// Integer shoppingId,
	// IItem randItem)
	// {
	// IItemDao<IItem> itemDao = getItemDaoImpl();
	// IItem relatedItem = itemDao.getRelated1( randItem );
	// return relatedItem;
	// }

//	@Transactional(readOnly=false)
	protected void refreshCart(IShoppingCart sc, List<Integer> ids,
	        List<Integer> quantities)
	{
		IShoppingCartLineDao sclDao = shoppingCartLineDao;
		for ( int i = 0; i < ids.size(); i++ )
		{
			int itemId = ids.get( i );
			int QTY = quantities.get( i );
			IShoppingCartLine scl = sclDao.getBySCandItem( sc.getScId(), itemId );
			if ( QTY == 0 )
			{
				sclDao.delete( scl );
			}
			else
			{
				scl.setSclQty( QTY );
				sclDao.shrani( scl );
			}
		}

	}

//	@Transactional(readOnly=false)
	protected void addItem(IShoppingCart sc, IItem item)
	{
		IShoppingCartLineDao sclDao = shoppingCartLineDao;
		IShoppingCartDao scDao = shoppingCartDao;

		IShoppingCartLine scl = sclDao.getBySCandItem( sc.getScId(),
		        item.getIId() );

		if ( scl != null )
		{
			scl.setSclQty( scl.getSclQty() + 1 );
			sclDao.shrani( scl );
		}
		else
		{
			scl = sclDao.getObject();
			scl.setItem( item );
			scl.setSclQty( 1 );
			
			scl.setShoppingCart( sc );
			sclDao.shrani( scl );
			
			sc.getShoppingCartLines().add( scl );
			scDao.shrani( sc );
		}
		
	}

	@Override
	@Transactional(readOnly=false)
	public BuyConfirmResult doBuyConfirm(Integer shoppingId,
	        Integer customerId, String ccType, long ccNumber, String ccName,
	        Date ccExpiry, String shipping, String street1, String street2,
	        String city, String state, String zip, String country)
	{

		IShoppingCart sc = shoppingCartDao.findById( shoppingId );
		ICustomer customer = customerDao.findById( customerId );

		double cDiscount = getCDiscount( customer );
//		IShoppingCart cart = getCart( shoppingId, cDiscount );
		IAddress address = saveAddress( street1, street2, city, state, zip,
		        country );
		
		IOrders order = saveOrder( address, customer, shipping, sc, cDiscount );

		saveCcXacts( order, ccType, ccNumber, ccName, ccExpiry, sc, address );
		
		clearCart( shoppingId );

		return new BuyConfirmResult( order, sc );
	}

	@Override
	@Transactional(readOnly=false)
	public BuyConfirmResult doBuyConfirm(Integer shoppingId,
	        Integer customerId, String ccType, Long ccNumber, String ccName,
	        Date ccExpiry, String shipping)
	{

		ICustomer customer = customerDao.findById( customerId );

		double discount = getCDiscount(  customer );
		IShoppingCart sc = getCart( shoppingId, discount );
		IAddress address = customer.getAddress();

		IOrders order = saveOrder( address, customer, shipping, sc, discount );
		
		saveCcXacts( order, ccType, ccNumber, ccName, ccExpiry, sc, address );

		clearCart( shoppingId );

		return new BuyConfirmResult( order, sc );
	}
	
	@Transactional(readOnly=false)
	protected void clearCart(Integer shoppingId)
	{
		IShoppingCartDao scDao = shoppingCartDao;
		IShoppingCart sc = (IShoppingCart) scDao.findById( shoppingId );

		IShoppingCartLineDao sclDao = shoppingCartLineDao;

		Set<IShoppingCartLine> res = sc.getShoppingCartLines();
		
		if( res != null && !res.isEmpty() )
		{
    		for ( IShoppingCartLine scl :  res )
    		{
    			sclDao.delete( scl );
    		}
		}
	}

    @Transactional(readOnly=false)
	protected ICcXacts saveCcXacts(IOrders order, String ccType, long ccNumber,
	        String ccName, Date ccExpiry, IShoppingCart cart, IAddress address)
	{
		if ( ccType.length() > 10 )
			ccType = ccType.substring( 0, 10 );

		if ( ccName.length() > 30 )
			ccName = ccName.substring( 0, 30 );

		ICcXacts ccXacts = ccXactsDao.getObject();
		ccXacts.setCountry( address.getCountry() );
		ccXacts.setOrders( order );
		ccXacts.setCxType( ccType );
		ccXacts.setCxNum( (int) ccNumber );
		ccXacts.setCxName( ccName );
		ccXacts.setCxExpiry( ccExpiry );
		ccXacts.setCxXactAmt( calculateTotal( cart ) );
		ccXacts.setCxXactDate( new Date() );
		ccXacts.setCountry( address.getCountry() );

		ccXactsDao.shrani( ccXacts );

		return ccXacts;
	}
	
	@Transactional(readOnly=false)
	protected IAddress saveAddress(String street1, String street2, String city,
	        String state, String zip, String country)
	{

		IAddress address = addressDao.getObject();

		address.setAddrStreet1( street1 );
		address.setAddrStreet2( street2 );
		address.setAddrCity( city );
		address.setAddrState( state );
		address.setAddrZip( zip );

		address.setCountry( countryDao.getByName( country ) );
		addressDao.shrani( address );

		return address;
	}
	
	@Transactional(readOnly=false)
	protected IOrders saveOrder(IAddress address, ICustomer customer,
	        String shipping, IShoppingCart sc, double discount)
	{

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
    		}
		}
		
		ordersDao.shrani( order );

		return order;
	}

	public String getRandomString(int min, int max)
	{
		String newstring = new String();
		Random rand = new Random();
		int i;
		final char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		        'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		        'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@', '#', '$', '%', '^',
		        '&', '*', '(', ')', '_', '-', '=', '+', '{', '}', '[', ']',
		        '|', ':', ';', ',', '.', '?', '/', '~', ' ' }; // 79
		                                                       // characters
		int strlen = (int) Math.floor( rand.nextDouble() * ( max - min + 1 ) );
		strlen += min;
		for ( i = 0; i < strlen; i++ )
		{
			char c = chars[(int) Math.floor( rand.nextDouble() * 79 )];
			newstring = newstring.concat( String.valueOf( c ) );
		}
		return newstring;
	}
	
	protected double calculateTotal(IShoppingCart sc)
	{
		double total = 0;
		Set<IShoppingCartLine> res = sc.getShoppingCartLines();
		
		if( res != null && !res.isEmpty() )
		{
    		for ( IShoppingCartLine scl : res )
    		{
    			if( scl != null )
    			{
    				total += scl.getItem().getICost();
    			}
    		}
		}

		return total;
	}

	protected double getCDiscount(ICustomer customer)
	{
		return customer.getCDiscount();
	}

	@Override
	public List searchByAuthor(String keyword)
	{

		List<IAuthor> authors = authorDao.findBySoundexLname( keyword );

		List<IItem> items = new ArrayList<IItem>();

		for ( IAuthor author : authors )
		{
			if ( items.size() >= 50 )
			{
				break;
			}

			items.addAll( itemDao.findAllByAuthor( author ) );
		}

		return items;
	}
	
	@Override
	public List<IItem> getPromotional()
	{
		return itemDao.getPromotional();
	}
	
	@Override
	public boolean countryExist( String country)
    {
		ICountry country1 = countryDao.getByName( country );
		return country1 == null;
    }
	
	@Override
	public ICustomer getUserBy(String uname, String passwd)
	{
	    return customerDao.getUserBy( uname, passwd );
	}
	
	@Override
	public ICustomer getCustomerObject()
	{
		return customerDao.getObject();
	}
	
	@Override
	public IAddress getAddressObject()
	{
		return addressDao.getObject();
	}
	
	@Override
	public ICountry getCountryByName(String country)
	{
		return countryDao.getByName( country );
	}
	
	@Override
	@Transactional(readOnly=false)
	public void saveAddress(IAddress address)
	{
	    addressDao.shrani(address);
	}
	
	@Override
	@Transactional(readOnly=false)
	public void saveCustomer(ICustomer customer)
	{
	    customerDao.shrani( customer );
	}
	
	@Override
	public IShoppingCart findShoppingCartById(Integer shoppingId)
	{
		return shoppingCartDao.findById( shoppingId );
	}
	
	@Override
	public ICustomer findCustomerById(Integer customerId)
	{
		return customerDao.findById( customerId );
	}
	
	@Override
	@Cacheable("bestSellers")
	public List<Object[]> getBestSellers(String category)
	{
		return itemDao.getBestSellers( category );
	}
	
	@Override
	@Cacheable("search")
    public List<IItem> searchByTitle(String keyword)
    {
	    List<IItem> items = itemDao.findAllByTitle( keyword );
	    
	    return items;
    }

	@Override
	@Cacheable("search")
    public List<IItem> searchBySubject(String keyword)
    {
	    List<IItem> items = itemDao.findAllBySubject( keyword );
	    
	    return items;
    }
	
	@Override
	public IOrders getMostRecentOrder(ICustomer customer)
	{
		return ordersDao.getMostRecentOrder( customer );
	}
	
	@Override
	public List<IOrderLine> findAllOrderLineByOrder(IOrders order)
	{
	    return orderLineDao.findAllByOrder( order );
	}
	@Override
	public IItem findItemById(Integer itemId)
	{
	    return itemDao.findById( itemId );
	}
	
	@Override
	public IAddress findAddressById(Integer addrId)
    {
	    return addressDao.findById( addrId );
    }
	
	@Override
	public ICountry getCountryById(Integer coId)
    {
	    return countryDao.findById( coId );
    }

	@Override
	public List<ICustomer> findCustomerByAddress(IAddress address)
    {
	    return customerDao.findByAddress( address );
    }

	@Override
	public IOrders findOrdersById(Integer cxOId)
    {
		return ordersDao.findById( cxOId );
    }

	@Override
	public IAuthor findAuthorById(Integer cxAuthId)
    {
		try 
		{
			return authorDao.findById( cxAuthId );
		}
		catch(Exception e)
		{
			System.out.println("AuthorDao is null");
			return null;
		}
    }

	@Override
	public IOrderLine findOrderLineById(Integer olId)
    {
		return orderLineDao.findById( olId );
    }

	@Override
	public ICcXacts findCcXactsById(Integer ccXactId)
    {
		return ccXactsDao.findById( ccXactId );
    }
	
	@Override
	public List findAllShoppingCartLinesBySC(IShoppingCart shoppingCart)
    {
		try 
		{
			return shoppingCartDao.findAllBySC( shoppingCart );
		}
		catch(NullPointerException e)
		{
			ShoppingCartDaoImpl bean = ContextHelper.getApplicationContext().getBean(ShoppingCartDaoImpl.class);
			return bean.findAllBySC(shoppingCart);
		}
    }
}
