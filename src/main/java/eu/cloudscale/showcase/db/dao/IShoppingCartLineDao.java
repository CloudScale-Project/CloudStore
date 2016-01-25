/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.db.dao;

import java.util.List;

import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;

public interface IShoppingCartLineDao extends IDao<IShoppingCartLine> 
{

	public IShoppingCartLine getBySCandItem(Integer shoppingId, int itemId);

	public void delete(IShoppingCartLine bySCandItem);

	public boolean isCartEmpty(int scId);

	public List<Object[]> findBySCId(Integer shoppingId);

}
