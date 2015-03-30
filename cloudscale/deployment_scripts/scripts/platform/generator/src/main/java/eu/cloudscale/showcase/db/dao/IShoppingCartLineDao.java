package eu.cloudscale.showcase.db.dao;

import java.util.List;

import eu.cloudscale.showcase.db.model.IShoppingCartLine;

public interface IShoppingCartLineDao extends IDao<IShoppingCartLine> 
{

	public IShoppingCartLine getBySCandItem(Integer shoppingId, int itemId);

	public void delete(IShoppingCartLine bySCandItem);

	public boolean isCartEmpty(int scId);

	public List<Object[]> findBySCId(Integer shoppingId);

}
