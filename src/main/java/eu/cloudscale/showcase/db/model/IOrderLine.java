/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/
package eu.cloudscale.showcase.db.model;


public interface IOrderLine
{

	public Integer getOlId();

	public void setOlId(Integer olId);

	public IOrders getOrders();

	public void setOrders(IOrders orders);

	public IItem getItem();

	public void setItem(IItem item);

	public Integer getOlQty();

	public void setOlQty(Integer olQty);

	public Double getOlDiscount();

	public void setOlDiscount(Double oL_DISCOUNT);

	public String getOlComment();

	public void setOlComment(String olComment);

}
