package eu.cloudscale.showcase.db;

import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.model.IShoppingCart;


public class BuyConfirmResult
{
	public IOrders order;
	public IShoppingCart cart;
	
	public BuyConfirmResult(IOrders order2, IShoppingCart sc)
    {
        order = order2;
        cart = sc;
    }

	
    public IOrders getOrder()
    {
    	return order;
    }

	
    public void setOrder(IOrders order)
    {
    	this.order = order;
    }

	
    public IShoppingCart getCart()
    {
    	return cart;
    }

	
    public void setCart(IShoppingCart cart)
    {
    	this.cart = cart;
    }
	
}
