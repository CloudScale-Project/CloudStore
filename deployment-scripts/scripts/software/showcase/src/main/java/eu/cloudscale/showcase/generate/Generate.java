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
		//db.populateCountryTable();
		//db.populateAuthorTable();
		db.populateAddressTable();
		//db.populateCustomerTable();
		//db.populateItemTable();
		//db.populateOrdersAndCC_XACTSTable();
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
	    IGenerate gen = args[0] == "mysql" ? context.getBean(GenerateHibernate.class) : context.getBean(GenerateMongo.class);
//	    gen.setContext(context);
		generate.generate(gen);
	}
}
