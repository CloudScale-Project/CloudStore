package eu.cloudscale.showcase.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

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
import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.ICcXacts;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;

@Transactional(readOnly=true)
public abstract class AService implements IService
{	
	@SuppressWarnings( "rawtypes" )
	@Override
	@Cacheable("newProducts")
	public List getNewProducts(String category)
	{
		return getItemDaoImpl().getNewProducts( category );
	}

	@Override
	@Transactional(readOnly=false)
	public IShoppingCart doCart(Integer shoppingId, Integer itemId,
	        List<Integer> ids, List<Integer> quantities)
	{

		IShoppingCartLineDao sclDao = getShoppingCartLineDaoImpl();
		IShoppingCartDao scDao = getShoppingCartDaoImpl();
		IItemDao itemDao = getItemDaoImpl();

		if ( itemId != null )
		{
			addItem( shoppingId, itemDao.findById( itemId.intValue() ) );
		}

		refreshCart( shoppingId, ids, quantities );
		addRandomItemToCartIfNecessary( sclDao, shoppingId );
		resetCartTime( sclDao, shoppingId );

		return scDao.findById( shoppingId );
	}
	
	@Transactional(readOnly=false)
	protected void addRandomItemToCartIfNecessary(IShoppingCartLineDao sclDao,
	        int SHOPPING_ID)
	{
		// check and see if the cart is empty. If it's not, we do
		// nothing.

		try
		{
			// Check to see if the cart is empty
			if ( sclDao.isCartEmpty( SHOPPING_ID ) )
			{
				// Cart is empty
				addItem( SHOPPING_ID, getItemDaoImpl().getRandomItem() );
			}

		}
		catch ( java.lang.Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	protected IShoppingCart getCart(Integer shoppingId, double discount)
	{
		IShoppingCartDao scDao = getShoppingCartDaoImpl();
		IShoppingCart sc = scDao.findById( shoppingId );
		return sc;
	}

	@Transactional(readOnly=false)
	protected void resetCartTime(IShoppingCartLineDao sclDao, Integer shoppingId)
	{
		IShoppingCartDao scDao = getShoppingCartDaoImpl();
		IShoppingCart sc = scDao.findById( shoppingId );
		sc.setScTime( new Date() );
		scDao.shrani( sc );
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

	@Transactional(readOnly=false)
	protected void refreshCart(Integer shoppingId, List<Integer> ids,
	        List<Integer> quantities)
	{
		IShoppingCartLineDao sclDao = getShoppingCartLineDaoImpl();
		for ( int i = 0; i < ids.size(); i++ )
		{
			int itemId = ids.get( i );
			int QTY = quantities.get( i );
			IShoppingCartLine scl = sclDao.getBySCandItem( shoppingId, itemId );
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

	@Transactional(readOnly=false)
	protected void addItem(Integer shoppingId, IItem item)
	{
		IShoppingCartLineDao sclDao = getShoppingCartLineDaoImpl();
		IShoppingCartDao scDao = getShoppingCartDaoImpl();

		IShoppingCartLine scl = sclDao.getBySCandItem( shoppingId,
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
			IShoppingCart sc = (IShoppingCart) scDao.findById( shoppingId );
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

		ICustomerDao customerDao = getCustomerDaoImpl();
		IShoppingCartDao shoppingCartDao = getShoppingCartDaoImpl();

		IShoppingCart sc = shoppingCartDao.findById( shoppingId );
		ICustomer customer = customerDao.findById( customerId );

		double cDiscount = getCDiscount( customerId );
		IShoppingCart cart = getCart( shoppingId, cDiscount );
		IAddress address = saveAddress( street1, street2, city, state, zip,
		        country );

		IOrders order = saveOrder( address, customer, shipping, sc, cDiscount );

		saveCcXacts( order, ccType, ccNumber, ccName, ccExpiry, cart, address );

		clearCart( shoppingId );

		return new BuyConfirmResult( order, sc );
	}

	@Override
	@Transactional(readOnly=false)
	public BuyConfirmResult doBuyConfirm(Integer shoppingId,
	        Integer customerId, String ccType, Long ccNumber, String ccName,
	        Date ccExpiry, String shipping)
	{
		double discount = getCDiscount( customerId );
		IShoppingCart sc = getCart( shoppingId, discount );

		ICustomerDao customerDao = getCustomerDaoImpl();
		ICustomer customer = customerDao.findById( customerId );

		IAddress address = customer.getAddress();

		IOrders order = saveOrder( address, customer, shipping, sc, discount );
		saveCcXacts( order, ccType, ccNumber, ccName, ccExpiry, sc, address );
		clearCart( shoppingId );

		return new BuyConfirmResult( order, sc );
	}
	
	@Transactional(readOnly=false)
	protected void clearCart(Integer shoppingId)
	{
		IShoppingCartDao scDao = getShoppingCartDaoImpl();
		IShoppingCart sc = (IShoppingCart) scDao.findById( shoppingId );

		IShoppingCartLineDao sclDao = getShoppingCartLineDaoImpl();

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

		ICcXactsDao ccXactsDao = getCcXactsDaoImpl();

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
		IAddressDao addressDao = getAddressDaoImpl();
		ICountryDao countryDao = getCountryDaoImpl();

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

	protected double getCDiscount(Integer customerId)
	{
		ICustomerDao customerDao = getCustomerDaoImpl();
		ICustomer customer = customerDao.findById( customerId );

		return customer.getCDiscount();
	}

	@Override
	public List searchByAuthor(String keyword)
	{
		IAuthorDao authorDao = getAuthorDaoImpl();
		IItemDao itemDao = getItemDaoImpl();

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

}
