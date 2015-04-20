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