package eu.cloudscale.showcase.generate;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import eu.cloudscale.showcase.db.common.DatabaseHelper;

public class Generate
{

	private IGenerate db;

	public Generate(String dbType)
	{

//		Resource resource = new ClassPathResource( "database.properties" );
//		Properties prop = null;
//		try
//		{
//			prop = PropertiesLoaderUtils.loadProperties( resource );
//		}
//		catch ( IOException e )
//		{
//			e.printStackTrace();
//		}
//
//		String dbType = prop.getProperty( "jdbc.dbtype" );

		if ( dbType.equalsIgnoreCase( "mongo" )
		        || dbType.equalsIgnoreCase( "mongodb" ) )
		{
			db = new GenerateMongo();
		}
		else
		{
			DatabaseHelper.loadMySQLDriver();
			db = new GenerateHibernate();
		}

	}

	public void generate()
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
		Generate generate = new Generate(args[0]);
		generate.generate();
	}
}
