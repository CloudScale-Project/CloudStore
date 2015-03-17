package eu.cloudscale.showcase.db.dao;

import java.util.List;

import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;

public interface IOrderLineDao extends IDao<IOrderLine> 
{

	List<IOrderLine> findAllByOrder(IOrders orders);

}
