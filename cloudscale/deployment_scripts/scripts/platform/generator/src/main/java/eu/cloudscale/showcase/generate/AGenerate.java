package eu.cloudscale.showcase.generate;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.IService;
import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.common.DatabaseHelper;
import eu.cloudscale.showcase.db.dao.IAddressDao;
import eu.cloudscale.showcase.db.dao.IAuthorDao;
import eu.cloudscale.showcase.db.dao.ICountryDao;
import eu.cloudscale.showcase.db.dao.ICustomerDao;
import eu.cloudscale.showcase.db.dao.IItemDao;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.ICountry;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.services.MongoService;


public abstract class AGenerate implements IGenerate
{

	// TODO: Move this to properties file
	protected static final int    NUM_EBS       = 100;
	
	// TODO: Move this to properties file
	protected static final int    NUM_ITEMS     = 10000;
	
	// TODO: Move this to properties file
	protected static final int    NUM_CUSTOMERS = NUM_EBS * 2880;
	
	// TODO: Move this to properties file
	protected static final int    NUM_ADDRESSES = 2 * NUM_CUSTOMERS;
	
	// TODO: Move this to properties file
	protected static final int    NUM_AUTHORS   = (int) ( .25 * NUM_ITEMS );
	
	// TODO: Move this to properties file
	protected static final int    NUM_ORDERS    = (int) ( .9 * NUM_CUSTOMERS );

	// TODO: Move this to properties file
	protected static String[][]   tables        = {
	        {"address", "addr_co_id", "addr_zip" }, {"author", "a_lname" },
	        {"cc_xacts" }, {"country", "co_name" },
	        {"customer", "c_addr_id", "c_uname" },
	        {"item", "i_title", "i_subject", "i_a_id" },
	        {"order_line", "ol_i_id", "ol_o_id" }, {"orders", "o_c_id" },
	        {"shopping_cart" }, {"shopping_cart_line", "scl_i_id" } };

	protected Random       rand          = new Random();
	
	protected GenericXmlApplicationContext ctx = null;
	
	protected IService db = null;
	
	public AGenerate()
	{
		ctx = ContextHelper.getApplicationContext();
		db = DatabaseHelper.getDatabase();
	}
	
	
	protected String getRandomAString(int min, int max)
	{
		String newstring = new String();
		int i;
		final char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		        'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		        'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@', '#', '$', '%', '^',
		        '&', '*', '(', ')', '_', '-', '=', '+', '{', '}', '[', ']',
		        '|', ':', ';', ',', '.', '?', '/', '~', ' ' }; // 79 characters
		int strlen = (int) Math
		        .floor( rand.nextDouble() * ( ( max - min ) + 1 ) );
		strlen += min;
		for ( i = 0; i < strlen; i++ )
		{
			char c = chars[(int) Math.floor( rand.nextDouble() * 79 )];
			newstring = newstring.concat( String.valueOf( c ) );
		}
		return newstring;
	}

	protected  String getRandomAString(int length)
	{
		String newstring = new String();
		int i;
		final char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		        'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		        'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@', '#', '$', '%', '^',
		        '&', '*', '(', ')', '_', '-', '=', '+', '{', '}', '[', ']',
		        '|', ':', ';', ',', '.', '?', '/', '~', ' ' }; // 79 characters
		for ( i = 0; i < length; i++ )
		{
			char c = chars[(int) Math.floor( rand.nextDouble() * 79 )];
			newstring = newstring.concat( String.valueOf( c ) );
		}
		return newstring;
	}

	protected  int getRandomNString(int num_digits)
	{
		int return_num = 0;
		for ( int i = 0; i < num_digits; i++ )
		{
			return_num += getRandomInt( 0, 9 )
			        * (int) java.lang.Math.pow( 10.0, (double) i );
		}
		return return_num;
	}

	protected  int getRandomNString(int min, int max)
	{
		int strlen = (int) Math
		        .floor( rand.nextDouble() * ( ( max - min ) + 1 ) ) + min;
		return getRandomNString( strlen );
	}

	protected  int getRandomInt(int lower, int upper)
	{

		int num = (int) Math.floor( rand.nextDouble()
		        * ( ( upper + 1 ) - lower ) );
		if ( num + lower > upper || num + lower < lower )
		{
			System.out.println( "ERROR: Random returned value of of range!" );
			System.exit( 1 );
		}
		return num + lower;
	}

	protected  String DigSyl(int D, int N)
	{
		int i;
		String resultString = new String();
		String Dstr = Integer.toString( D );

		if ( N > Dstr.length() )
		{
			int padding = N - Dstr.length();
			for ( i = 0; i < padding; i++ )
				resultString = resultString.concat( "BA" );
		}

		for ( i = 0; i < Dstr.length(); i++ )
		{
			if ( Dstr.charAt( i ) == '0' )
				resultString = resultString.concat( "BA" );
			else if ( Dstr.charAt( i ) == '1' )
				resultString = resultString.concat( "OG" );
			else if ( Dstr.charAt( i ) == '2' )
				resultString = resultString.concat( "AL" );
			else if ( Dstr.charAt( i ) == '3' )
				resultString = resultString.concat( "RI" );
			else if ( Dstr.charAt( i ) == '4' )
				resultString = resultString.concat( "RE" );
			else if ( Dstr.charAt( i ) == '5' )
				resultString = resultString.concat( "SE" );
			else if ( Dstr.charAt( i ) == '6' )
				resultString = resultString.concat( "AT" );
			else if ( Dstr.charAt( i ) == '7' )
				resultString = resultString.concat( "UL" );
			else if ( Dstr.charAt( i ) == '8' )
				resultString = resultString.concat( "IN" );
			else if ( Dstr.charAt( i ) == '9' )
				resultString = resultString.concat( "NG" );
		}

		return resultString;
	}
	
	@Override
	@Transactional(readOnly=false)
    public void populateCountryTable()
	{
		String[] countries = {"United States", "United Kingdom", "Canada",
		        "Germany", "France", "Japan", "Netherlands", "Italy",
		        "Switzerland", "Australia", "Algeria", "Argentina", "Armenia",
		        "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangla Desh",
		        "Barbados", "Belarus", "Belgium", "Bermuda", "Bolivia",
		        "Botswana", "Brazil", "Bulgaria", "Cayman Islands", "Chad",
		        "Chile", "China", "Christmas Island", "Colombia", "Croatia",
		        "Cuba", "Cyprus", "Czech Republic", "Denmark",
		        "Dominican Republic", "Eastern Caribbean", "Ecuador", "Egypt",
		        "El Salvador", "Estonia", "Ethiopia", "Falkland Island",
		        "Faroe Island", "Fiji", "Finland", "Gabon", "Gibraltar",
		        "Greece", "Guam", "Hong Kong", "Hungary", "Iceland", "India",
		        "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Jamaica",
		        "Jordan", "Kazakhstan", "Kuwait", "Lebanon", "Luxembourg",
		        "Malaysia", "Mexico", "Mauritius", "New Zealand", "Norway",
		        "Pakistan", "Philippines", "Poland", "Portugal", "Romania",
		        "Russia", "Saudi Arabia", "Singapore", "Slovakia",
		        "South Africa", "South Korea", "Spain", "Sudan", "Sweden",
		        "Taiwan", "Thailand", "Trinidad", "Turkey", "Venezuela",
		        "Zambia" };

		double[] exchanges = {1, .625461, 1.46712, 1.86125, 6.24238, 121.907,
		        2.09715, 1842.64, 1.51645, 1.54208, 65.3851, 0.998, 540.92,
		        13.0949, 3977, 1, .3757, 48.65, 2, 248000, 38.3892, 1, 5.74,
		        4.7304, 1.71, 1846, .8282, 627.1999, 494.2, 8.278, 1.5391,
		        1677, 7.3044, 23, .543, 36.0127, 7.0707, 15.8, 2.7, 9600,
		        3.33771, 8.7, 14.9912, 7.7, .6255, 7.124, 1.9724, 5.65822,
		        627.1999, .6255, 309.214, 1, 7.75473, 237.23, 74.147, 42.75,
		        8100, 3000, .3083, .749481, 4.12, 37.4, 0.708, 150, .3062,
		        1502, 38.3892, 3.8, 9.6287, 25.245, 1.87539, 7.83101, 52,
		        37.8501, 3.9525, 190.788, 15180.2, 24.43, 3.7501, 1.72929,
		        43.9642, 6.25845, 1190.15, 158.34, 5.282, 8.54477, 32.77,
		        37.1414, 6.1764, 401500, 596, 2447.7 };

		String[] currencies = {"Dollars", "Pounds", "Dollars",
		        "Deutsche Marks", "Francs", "Yen", "Guilders", "Lira",
		        "Francs", "Dollars", "Dinars", "Pesos", "Dram", "Schillings",
		        "Manat", "Dollars", "Dinar", "Taka", "Dollars", "Rouble",
		        "Francs", "Dollars", "Boliviano", "Pula", "Real", "Lev",
		        "Dollars", "Franc", "Pesos", "Yuan Renmimbi", "Dollars",
		        "Pesos", "Kuna", "Pesos", "Pounds", "Koruna", "Kroner",
		        "Pesos", "Dollars", "Sucre", "Pounds", "Colon", "Kroon",
		        "Birr", "Pound", "Krone", "Dollars", "Markka", "Franc",
		        "Pound", "Drachmas", "Dollars", "Dollars", "Forint", "Krona",
		        "Rupees", "Rupiah", "Rial", "Dinar", "Punt", "Shekels",
		        "Dollars", "Dinar", "Tenge", "Dinar", "Pounds", "Francs",
		        "Ringgit", "Pesos", "Rupees", "Dollars", "Kroner", "Rupees",
		        "Pesos", "Zloty", "Escudo", "Leu", "Rubles", "Riyal",
		        "Dollars", "Koruna", "Rand", "Won", "Pesetas", "Dinar",
		        "Krona", "Dollars", "Baht", "Dollars", "Lira", "Bolivar",
		        "Kwacha" };

		int NUM_COUNTRIES = 92;

		System.out.println( "Populating COUNTRY with " + NUM_COUNTRIES
		        + " countries" );
		
		ICountryDao countryDao = db.getCountryDaoImpl();
		countryDao.createTable();
		
		for ( int i = 1; i <= NUM_COUNTRIES; i++ )
		{
			ICountry c = countryDao.getObject();
			
			if( db instanceof MongoService)
			{
				c.setCoId( i );
			}
			c.setCoName( countries[i - 1] );
			c.setCoExchange( exchanges[i - 1] );
			c.setCoCurrency( currencies[i - 1] );

			countryDao.shrani( c );
		}

		countryDao.finish();

		//System.out.println( "" );
    }


	@Override
    public void populateAuthorTable()
    {
		String A_FNAME, A_MNAME, A_LNAME, A_BIO;
		java.sql.Date A_DOB;
		GregorianCalendar cal;

		System.out.println( "Populating AUTHOR Table with " + NUM_AUTHORS
		        + " authors" );
		
		IAuthorDao authorDao = db.getAuthorDaoImpl();
		
		for ( int i = 1; i <= NUM_AUTHORS; i++ )
		{
			IAuthor a = authorDao.getObject();

			int month, day, year, maxday;
			A_FNAME = getRandomAString( 3, 20 );
			A_MNAME = getRandomAString( 1, 20 );
			A_LNAME = getRandomAString( 1, 20 );
			year = getRandomInt( 1800, 1990 );
			month = getRandomInt( 0, 11 );
			maxday = 31;
			if ( month == 3 | month == 5 | month == 8 | month == 10 )
				maxday = 30;
			else if ( month == 1 )
				maxday = 28;
			day = getRandomInt( 1, maxday );
			cal = new GregorianCalendar( year, month, day );
			A_DOB = new java.sql.Date( cal.getTime().getTime() );
			A_BIO = getRandomAString( 125, 500 );

			// MongoDB doesn't have autoincrement field so we must set ID manually
			if( db instanceof MongoService)
			{
				a.setAId( i );
			}
			a.setAFname( A_FNAME );
			a.setALname( A_LNAME );
			a.setAMname( A_MNAME );
			a.setADob( A_DOB );
			a.setABio( A_BIO );

			authorDao.shrani( a );
		}

		authorDao.finish();

		System.out.println( "" );    
    }


	@Override
    public void populateAddressTable()
    {
		System.out.println( "Populating ADDRESS Table with " + NUM_ADDRESSES
		        + " addresses" );
		System.out.print( "Complete (in 10,000's): " );
		String ADDR_STREET1, ADDR_STREET2, ADDR_CITY, ADDR_STATE;
		String ADDR_ZIP;
		int ADDR_CO_ID;
		
		IAddressDao addressDao = db.getAddressDaoImpl();
		ICountryDao countryDao = db.getCountryDaoImpl();

		for ( int i = 1; i <= NUM_ADDRESSES; i++ )
		{
			IAddress a = addressDao.getObject();

			if ( i % 10000 == 0 )
				System.out.print( i / 10000 + " " );

			ADDR_STREET1 = getRandomAString( 15, 40 );
			ADDR_STREET2 = getRandomAString( 15, 40 );
			ADDR_CITY = getRandomAString( 4, 30 );
			ADDR_STATE = getRandomAString( 2, 20 );
			ADDR_ZIP = getRandomAString( 5, 10 );
			ADDR_CO_ID = getRandomInt( 1, 92 );

			// MongoDB doesn't have autoincrement field so we must set ID manually
			if( db instanceof MongoService)
			{
				a.setAddrId( i );
			}
			a.setAddrStreet1( ADDR_STREET1 );
			a.setAddrStreet2( ADDR_STREET2 );
			a.setAddrCity( ADDR_CITY );
			a.setAddrState( ADDR_STATE );
			a.setAddrZip( ADDR_ZIP );
			a.setCountry( countryDao.findById( ADDR_CO_ID ) );

			addressDao.shrani( a );
		}

		addressDao.finish();

		System.out.println( "" );
    }


	@Override
    public void populateCustomerTable()
    {
		String C_UNAME, C_PASSWD, C_LNAME, C_FNAME;
		int C_ADDR_ID, C_PHONE;
		String C_EMAIL;
		java.sql.Date C_SINCE, C_LAST_LOGIN;
		java.sql.Timestamp C_LOGIN, C_EXPIRATION;
		double C_DISCOUNT, C_BALANCE, C_YTD_PMT;
		java.sql.Date C_BIRTHDATE;
		String C_DATA;
		int i;

		System.out.println( "Populating CUSTOMER Table with " + NUM_CUSTOMERS
		        + " customers" );
		System.out.print( "Complete (in 10,000's): " );
		
		ICustomerDao customerDao = db.getCustomerDaoImpl();
		IAddressDao addressDao = db.getAddressDaoImpl();

		for ( i = 1; i <= NUM_CUSTOMERS; i++ )
		{
			ICustomer c = customerDao.getObject();

			if ( i % 10000 == 0 )
				System.out.print( i / 10000 + " " );
			
			C_UNAME = DigSyl( i, 0 ).toLowerCase();
			C_PASSWD = C_UNAME.toLowerCase();
			C_LNAME = getRandomAString( 8, 15 );
			C_FNAME = getRandomAString( 8, 15 );
			C_ADDR_ID = getRandomInt( 1, NUM_ADDRESSES );
			C_PHONE = getRandomNString( 9, 16 );
			C_EMAIL = C_UNAME + "@" + getRandomAString( 2, 9 ) + ".com";

			GregorianCalendar cal = new GregorianCalendar();
			cal.add( Calendar.DAY_OF_YEAR, -1 * getRandomInt( 1, 730 ) );
			C_SINCE = new java.sql.Date( cal.getTime().getTime() );
			cal.add( Calendar.DAY_OF_YEAR, getRandomInt( 0, 60 ) );
			if ( cal.after( new GregorianCalendar() ) )
				cal = new GregorianCalendar();

			C_LAST_LOGIN = new java.sql.Date( cal.getTime().getTime() );
			C_LOGIN = new java.sql.Timestamp( System.currentTimeMillis() );
			cal = new GregorianCalendar();
			cal.add( Calendar.HOUR, 2 );
			C_EXPIRATION = new java.sql.Timestamp( cal.getTime().getTime() );

			C_DISCOUNT = (double) getRandomInt( 0, 50 ) / 100.0;
			C_BALANCE = 0.00;
			C_YTD_PMT = (double) getRandomInt( 0, 99999 ) / 100.0;
			int year = getRandomInt( 1880, 2000 );
			int month = getRandomInt( 0, 11 );
			int maxday = 31;
			int day;
			if ( month == 3 | month == 5 | month == 8 | month == 10 )
				maxday = 30;
			else if ( month == 1 )
				maxday = 28;
			day = getRandomInt( 1, maxday );
			cal = new GregorianCalendar( year, month, day );
			C_BIRTHDATE = new java.sql.Date( cal.getTime().getTime() );

			C_DATA = getRandomAString( 100, 500 );

			// MongoDB doesn't have autoincrement field so we must set ID manually
    		if( db instanceof MongoService)
    		{
    			c.setCId( i );
    		}
			c.setCUname( C_UNAME );
			c.setCPasswd( C_PASSWD );
			c.setCFname( C_FNAME );
			c.setCLname( C_LNAME );
			IAddress addr = addressDao.findById( C_ADDR_ID );
			c.setAddress( addr );
			c.setCPhone( String.valueOf( C_PHONE ) );
			c.setCEmail( C_EMAIL );
			c.setCSince( C_SINCE );
			c.setCLastVisit( C_LAST_LOGIN );
			c.setCLogin( C_LOGIN );
			c.setCExpiration( C_EXPIRATION );
			c.setCDiscount( C_DISCOUNT );
			c.setCBalance( C_BALANCE );
			c.setCYtdPmt( C_YTD_PMT );
			c.setCBirthdate( C_BIRTHDATE );
			c.setCData( C_DATA );

			customerDao.shrani( c );
		}

		customerDao.finish();

		//System.out.print( "\n" );
    }


	@Override
    public void populateItemTable()
    {
		String I_TITLE;
		GregorianCalendar cal;
		int I_A_ID;
		java.sql.Date I_PUB_DATE;
		String I_PUBLISHER, I_SUBJECT, I_DESC;
		int I_RELATED1, I_RELATED2, I_RELATED3, I_RELATED4, I_RELATED5;
		String I_THUMBNAIL, I_IMAGE;
		double I_SRP, I_COST, I_RANDOM;
		java.sql.Date I_AVAIL;
		int I_STOCK;
		String I_ISBN;
		int I_PAGE;
		String I_BACKING;
		String I_DIMENSIONS;

		String[] SUBJECTS = {"ARTS", "BIOGRAPHIES", "BUSINESS", "CHILDREN",
		        "COMPUTERS", "COOKING", "HEALTH", "HISTORY", "HOME", "HUMOR",
		        "LITERATURE", "MYSTERY", "NON-FICTION", "PARENTING",
		        "POLITICS", "REFERENCE", "RELIGION", "ROMANCE", "SELF-HELP",
		        "SCIENCE-NATURE", "SCIENCE-FICTION", "SPORTS", "YOUTH",
		        "TRAVEL" };
		int NUM_SUBJECTS = 24;

		String[] BACKINGS = {"HARDBACK", "PAPERBACK", "USED", "AUDIO",
		        "LIMITED-EDITION" };
		int NUM_BACKINGS = 5;

		System.out.println( "Populating ITEM table with " + NUM_ITEMS
		        + " items" );
		
		IItemDao itemDao = db.getItemDaoImpl();
		IAuthorDao authorDao = db.getAuthorDaoImpl();
		
		Random rand = new Random();
		
		for ( int i = 1; i <= NUM_ITEMS; i++ )
		{
			I_RANDOM = rand.nextDouble();
			int month, day, year, maxday;
			I_TITLE = getRandomAString( 14, 60 );
			if ( i <= ( NUM_ITEMS / 4 ) )
				I_A_ID = i;
			else
				I_A_ID = getRandomInt( 1, NUM_ITEMS / 4 );

			year = getRandomInt( 1930, 2000 );
			month = getRandomInt( 0, 11 );
			maxday = 31;
			if ( month == 3 | month == 5 | month == 8 | month == 10 )
				maxday = 30;
			else if ( month == 1 )
				maxday = 28;
			day = getRandomInt( 1, maxday );
			cal = new GregorianCalendar( year, month, day );
			I_PUB_DATE = new java.sql.Date( cal.getTime().getTime() );

			I_PUBLISHER = getRandomAString( 14, 60 );
			I_SUBJECT = SUBJECTS[getRandomInt( 0, NUM_SUBJECTS - 1 )];
			I_DESC = getRandomAString( 100, 500 );

			I_RELATED1 = getRandomInt( 1, NUM_ITEMS );
			do
			{
				I_RELATED2 = getRandomInt( 1, NUM_ITEMS );
			}
			while ( I_RELATED2 == I_RELATED1 );
			do
			{
				I_RELATED3 = getRandomInt( 1, NUM_ITEMS );
			}
			while ( I_RELATED3 == I_RELATED1 || I_RELATED3 == I_RELATED2 );
			do
			{
				I_RELATED4 = getRandomInt( 1, NUM_ITEMS );
			}
			while ( I_RELATED4 == I_RELATED1 || I_RELATED4 == I_RELATED2
			        || I_RELATED4 == I_RELATED3 );
			do
			{
				I_RELATED5 = getRandomInt( 1, NUM_ITEMS );
			}
			while ( I_RELATED5 == I_RELATED1 || I_RELATED5 == I_RELATED2
			        || I_RELATED5 == I_RELATED3 || I_RELATED5 == I_RELATED4 );

			I_THUMBNAIL = new String( "img" + i % 100 + "/thumb_" + i + ".gif" );
			I_IMAGE = new String( "img" + i % 100 + "/image_" + i + ".gif" );
			I_SRP = (double) getRandomInt( 100, 99999 );
			I_SRP /= 100.0;

			I_COST = I_SRP
			        - ( ( ( (double) getRandomInt( 0, 50 ) / 100.0 ) ) * I_SRP );

			cal.add( Calendar.DAY_OF_YEAR, getRandomInt( 1, 30 ) );
			I_AVAIL = new java.sql.Date( cal.getTime().getTime() );
			I_STOCK = getRandomInt( 10, 30 );
			I_ISBN = getRandomAString( 13 );
			I_PAGE = getRandomInt( 20, 9999 );
			I_BACKING = BACKINGS[getRandomInt( 0, NUM_BACKINGS - 1 )];
			I_DIMENSIONS = ( (double) getRandomInt( 1, 9999 ) / 100.0 ) + "x"
			        + ( (double) getRandomInt( 1, 9999 ) / 100.0 ) + "x"
			        + ( (double) getRandomInt( 1, 9999 ) / 100.0 );

			IItem item = itemDao.getObject();
			// Set parameter
			// MongoDB doesn't have autoincrement field so we must set ID manually
			if( db instanceof MongoService)
			{
				item.setIId( i );
			}
			item.setIRandom( I_RANDOM );
			item.setITitle( I_TITLE );
			item.setIPubDate( I_PUB_DATE );
			item.setIPublisher( I_PUBLISHER );
			item.setISubject( I_SUBJECT );
			item.setIDesc( I_DESC );
			item.setIRelated1( I_RELATED1 );
			item.setIRelated2( I_RELATED2 );
			item.setIRelated3( I_RELATED3 );
			item.setIRelated4( I_RELATED4 );
			item.setIRelated5( I_RELATED5 );
			item.setIThumbnail( I_THUMBNAIL );
			item.setIImage( I_IMAGE );
			item.setISrp( I_SRP );
			item.setICost( I_COST );
			item.setIAvail( I_AVAIL );
			item.setIStock( I_STOCK );
			item.setIIsbn( I_ISBN );
			item.setIPage( String.valueOf( I_PAGE ) );
			item.setIBacking( I_BACKING );
			item.setIDimension( I_DIMENSIONS );
			item.setAuthor( authorDao.findById( I_A_ID ) );

			itemDao.shrani( item );
			
			itemDao.findById( item.getIId() );
		}

		itemDao.finish();
		//System.out.println( "" );
    }
	
}
