/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/

package eu.cloudscale.showcase.db;

import eu.cloudscale.showcase.db.dao.IAddressDao;
import eu.cloudscale.showcase.db.dao.ICustomerDao;
import eu.cloudscale.showcase.db.dao.IItemDao;
import eu.cloudscale.showcase.db.dao.IShoppingCartDao;
import eu.cloudscale.showcase.db.dao.IShoppingCartLineDao;


public interface IDao
{
	
	public IItemDao getItemDao();

	public IShoppingCartLineDao getShoppingCartLineDao();

	public IShoppingCartDao getShoppingCartDao();

	public IAddressDao getAddressDao();

	public ICustomerDao getCustomerDao();

	public IShoppingCartLineDao getShoppingCartLineDaoImpl();
	 
}
