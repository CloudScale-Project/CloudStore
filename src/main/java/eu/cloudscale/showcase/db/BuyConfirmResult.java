/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

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
