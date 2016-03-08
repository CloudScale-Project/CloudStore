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

import org.springframework.context.support.GenericXmlApplicationContext;


public class ContextHelper 
{
	private static GenericXmlApplicationContext ctx = null;
	
	public static GenericXmlApplicationContext getApplicationContext()
	{
		if( ctx == null)
		{
			ctx = new GenericXmlApplicationContext();
			ctx.load("classpath:app-context.xml");
			ctx.refresh();
		}
		
		return ctx;
	}
	
}
