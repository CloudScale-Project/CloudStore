/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.db.model;

import java.util.Date;
import java.util.Set;


public interface IShoppingCart
{

	public void setScTime(Date scTime);

	public Date getScTime();

	public void setScId(Integer scId);

	public Integer getScId();

	public Set<IShoppingCartLine> getShoppingCartLines();

}
