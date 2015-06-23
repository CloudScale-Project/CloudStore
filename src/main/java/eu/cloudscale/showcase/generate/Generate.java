package eu.cloudscale.showcase.generate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import eu.cloudscale.showcase.db.dao.IAddressDao;

@Component
public class Generate
{
	public Generate()
	{
		
	}

	public void generate(IGenerate db)
	{

		//db.dropTables( tables );
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
		if( args.length < 1 )
		{
			System.out.println("Usage: $ java Generate <mysql|mongodb>");
			System.exit(0);			
		}

		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:app-context.xml");
		
	    Generate generate = context.getBean(Generate.class);
    
	    IGenerate db = (IGenerate) context.getBean("generateMongo");
	    String db_str = "Generating for MongoDB";
	    if(args[0].equals("mysql"))
	    {
	    	db_str = "Generating for MySQL";
	    	db = (IGenerate) context.getBean("generateHibernate");
	    }
	    System.out.println(db_str);
		generate.generate(db);
	}	
}