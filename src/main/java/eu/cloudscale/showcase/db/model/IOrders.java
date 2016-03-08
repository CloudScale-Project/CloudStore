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

import java.util.Date;
import java.util.Set;

public interface IOrders
{

	public Integer getOId();

	public void setOId(Integer OId);

	IAddress getAddressByOShipAddrId();

	void setAddressByOShipAddrId(IAddress addressByOShipAddrId);

	ICustomer getCustomer();

	void setCustomer(ICustomer customer);

	IAddress getAddressByOBillAddrId();

	void setAddressByOBillAddrId(IAddress addressByOBillAddrId);


	void setOStatus(String OStatus);

	String getOStatus();

	void setOShipDate(Date OShipDate);

	Date getOShipDate();

	void setOShipType(String OShipType);

	String getOShipType();

	void setOTotal(double o_TOTAL);

	Double getOTotal();

	void setOTax(Double o_TAX);

	Double getOTax();

	void setOSubTotal(Double o_SUB_TOTAL);

	Double getOSubTotal();

	void setODate(Date ODate);

	Date getODate();

	public Set<IOrderLine> getOrderLines();

}
