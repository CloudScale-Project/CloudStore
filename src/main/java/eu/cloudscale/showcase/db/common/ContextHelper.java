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
