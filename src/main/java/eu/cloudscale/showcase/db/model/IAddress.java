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

import java.util.Set;



public interface IAddress
{

	public Integer getAddrId();

	public void setAddrId(Integer addrId);

	public  ICountry getCountry();

	public void setCountry(ICountry country);

	public String getAddrStreet1();

	public void setAddrStreet1(String addrStreet1);

	public String getAddrStreet2();

	public void setAddrStreet2(String addrStreet2);

	public String getAddrCity();

	public void setAddrCity(String addrCity);

	public String getAddrState();

	public void setAddrState(String addrState);

	public String getAddrZip();

	public void setAddrZip(String addrZip);

	public Set<ICustomer> getCustomers();

}
