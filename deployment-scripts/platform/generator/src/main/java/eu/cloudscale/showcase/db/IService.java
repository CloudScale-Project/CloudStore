package eu.cloudscale.showcase.db;

import java.util.Date;
import java.util.List;

import eu.cloudscale.showcase.db.dao.ICustomerDao;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.IShoppingCart;

public interface IService extends IDaos
{
	
    public List getNewProducts(String category);

	public IShoppingCart doCart(Integer shoppingId, Integer itemId,
            List<Integer> ids, List<Integer> quantities);

	public BuyConfirmResult doBuyConfirm(Integer shoppingId, Integer customerId,
            String ccType, long ccNumber, String ccName, Date ccExpiry,
            String shipping, String street1, String street2, String city,
            String state, String zip, String country);

	public BuyConfirmResult doBuyConfirm(Integer shoppingId, Integer customerId,
            String ccType, Long ccNumber, String ccName, Date ccExpiry,
            String shipping);

	public ICustomerDao getCustomerDaoImpl();

	public List<IItem> searchByAuthor(String keyword);

	public List<IItem> searchByTitle(String keyword);

	public List<IItem> searchBySubject(String keyword);

	public List<Object[]> getBestSellers(String category);	
	
}
