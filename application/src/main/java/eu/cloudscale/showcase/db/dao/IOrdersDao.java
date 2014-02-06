package eu.cloudscale.showcase.db.dao;

import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IOrders;

public interface IOrdersDao extends IDao<IOrders> 
{
	public IOrders getMostRecentOrder(ICustomer customer);

	public IOrders findById(Integer cxOId);
}
