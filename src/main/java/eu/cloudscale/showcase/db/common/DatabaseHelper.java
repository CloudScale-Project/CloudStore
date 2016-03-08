/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/
package eu.cloudscale.showcase.db.common;

import eu.cloudscale.showcase.db.services.IService;


public class DatabaseHelper
{
	
    public static IService getDatabase()
	{

    	IService db = (IService) ContextHelper.getApplicationContext().getBean( "service" );
    	if (db == null)
    	{
    		System.out.println("Service is null");
    	}
		return db;

	}
	
}