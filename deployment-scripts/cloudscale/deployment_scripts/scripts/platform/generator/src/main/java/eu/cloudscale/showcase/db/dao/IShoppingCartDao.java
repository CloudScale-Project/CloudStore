package eu.cloudscale.showcase.db.dao;

import java.util.List;

import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;


public interface IShoppingCartDao extends IDao<IShoppingCart> 
{
	public Integer createEmptyCart();

	public IShoppingCart findById(Integer shoppingId);

	public List<IShoppingCartLine> findAllBySC(IShoppingCart shoppingCart);
}
