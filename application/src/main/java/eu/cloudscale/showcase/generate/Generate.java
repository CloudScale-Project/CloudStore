package eu.cloudscale.showcase.generate;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;




import eu.cloudscale.showcase.db.common.DatabaseHelper;

public class Generate
{

	private IGenerate db;
	
	public Generate()
	{

	}

	public void generate(IGenerate db)
	{

//		 db.dropTables( tables );
		 db.populateCountryTable();
		 db.populateAuthorTable();
		 db.populateAddressTable();
		 db.populateCustomerTable();
		 db.populateItemTable();
		 db.populateOrdersAndCC_XACTSTable();
		// db.createIndexes(tables);

		System.out.println( "FINISHED!" );
	}

	public static void main(String[] args)
	{
//		if( args.length < 1 )
//		{
//			System.out.println("Usage: $ java Generate <mysql|mongodb>");
//			System.exit(0);			
//		}

		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:app-context.xml");
		
	    Generate generate = (Generate) context.getBean("generator");
	    
	    IGenerate gen = (IGenerate) context.getBean("generate");
	    gen.setContext(context);
	    try
	    {
			generate.generate(gen);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	System.out.println("Have you uncommented <prop key=\"hibernate.hbm2ddl.auto\">create</prop> in hibernate.xml?");
	    }
	}
}
