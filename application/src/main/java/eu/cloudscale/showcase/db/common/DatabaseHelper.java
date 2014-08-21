package eu.cloudscale.showcase.db.common;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import eu.cloudscale.showcase.db.IService;


public class DatabaseHelper
{
	
	private static IService db = null;
	private static String driverName = null;
	
    public static IService getDatabase()
	{
		if( db == null )
		{
    			db = (IService) ContextHelper.getApplicationContext().getBean( "service" );
		}
		
		return db;

	}
	
	public static String getDriverName()
	{
		return driverName;
	}
	
	public static void loadMySQLDriver()
    {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	    
    }
	
}
