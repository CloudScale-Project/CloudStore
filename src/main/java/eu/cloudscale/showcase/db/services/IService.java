package eu.cloudscale.showcase.db.services;

import java.util.Date;
import java.util.List;

import eu.cloudscale.showcase.db.BuyConfirmResult;
import eu.cloudscale.showcase.db.dao.ICustomerDao;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.ICcXacts;
import eu.cloudscale.showcase.db.model.ICountry;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.model.IShoppingCart;

public interface IService 
{
	
    public List getNewProducts(String category);

	public IShoppingCart doCart(IShoppingCart sc, Integer itemId,
            List<Integer> ids, List<Integer> quantities);

	public BuyConfirmResult doBuyConfirm(Integer shoppingId, Integer customerId,
            String ccType, long ccNumber, String ccName, Date ccExpiry,
            String shipping, String street1, String street2, String city,
            String state, String zip, String country);

	public BuyConfirmResult doBuyConfirm(Integer shoppingId, Integer customerId,
            String ccType, Long ccNumber, String ccName, Date ccExpiry,
            String shipping);

	public List<IItem> searchByAuthor(String keyword);

	public List<IItem> searchByTitle(String keyword);

	public List<IItem> searchBySubject(String keyword);

	public List<Object[]> getBestSellers(String category);

	public IShoppingCart createEmptyCart();

	public IShoppingCart findShoppingCartById(Integer shoppingId);

	public List<IItem> getPromotional();

	boolean countryExist(String country);

	public ICustomer getUserBy(String uname, String passwd);

	public ICustomer getCustomerObject();

	public IAddress getAddressObject();

	public ICountry getCountryByName(String country);

	public void saveAddress(IAddress address);

	public void saveCustomer(ICustomer customer);

	public ICustomer findCustomerById(Integer customerId);

	public IOrders getMostRecentOrder(ICustomer customer);

	public List<IOrderLine> findAllOrderLineByOrder(IOrders order);

	public IItem findItemById(Integer itemId);

	List findAllShoppingCartLinesBySC(IShoppingCart shoppingCart);

	ICcXacts findCcXactsById(Integer ccXactId);

	IOrderLine findOrderLineById(Integer olId);

	IAuthor findAuthorById(Integer cxAuthId);

	IOrders findOrdersById(Integer cxOId);

	List<ICustomer> findCustomerByAddress(IAddress address);

	ICountry getCountryById(Integer coId);

	IAddress findAddressById(Integer addrId);

	public void saveItem(IItem item);
	
}
