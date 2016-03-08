/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/
package eu.cloudscale.showcase.db.dao;

import java.util.List;

import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;


public interface IShoppingCartDao extends IDao<IShoppingCart> 
{
//	public Integer createEmptyCart();

	public IShoppingCart findById(Integer shoppingId);

	public List<IShoppingCartLine> findAllBySC(IShoppingCart shoppingCart);
}
