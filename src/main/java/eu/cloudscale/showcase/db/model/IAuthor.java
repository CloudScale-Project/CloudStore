/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.db.model;

import java.util.Date;



public interface IAuthor
{

	public Integer getAId();

	public void setAId(Integer AId);

	public String getAFname();

	public void setAFname(String AFname);

	public String getALname();

	public void setALname(String ALname);

	public String getAMname();

	public void setAMname(String AMname);

	public Date getADob();

	public void setADob(Date ADob);

	public String getABio();

	public void setABio(String ABio);

}
