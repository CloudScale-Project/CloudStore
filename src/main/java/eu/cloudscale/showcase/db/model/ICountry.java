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


public interface ICountry
{

	public Integer getCoId();

	public void setCoId(Integer coId);

	public String getCoName();

	public void setCoName(String coName);

	public Double getCoExchange();

	public void setCoExchange(Double coExchange);

	public String getCoCurrency();

	public void setCoCurrency(String coCurrency);

}
