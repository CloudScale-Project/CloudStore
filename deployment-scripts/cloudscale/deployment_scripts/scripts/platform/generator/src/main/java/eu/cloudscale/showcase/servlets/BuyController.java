package eu.cloudscale.showcase.servlets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.cloudscale.showcase.db.dao.IAddressDao;
import eu.cloudscale.showcase.db.dao.ICountryDao;
import eu.cloudscale.showcase.db.dao.ICustomerDao;
import eu.cloudscale.showcase.db.dao.IShoppingCartDao;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICountry;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;

@Controller
@RequestMapping( "/buy" )
public class BuyController extends AController
{
	
	@RequestMapping( value = "", method = RequestMethod.GET )
	@Transactional
	public String get(
	        @RequestParam( value = "SHOPPING_ID", required = false ) Integer shoppingId,
	        @RequestParam( value = "C_ID", required = false ) Integer customerId,
	        @RequestParam( value = "RETURNING_FLAG", required = false ) String returningFlag,
	        HttpServletRequest request, Model model)
	{
		HttpSession session = request.getSession(false);
		
		
		
		ArrayList<String> errors = new ArrayList<String>();

		ICustomerDao customerDao = service.getCustomerDaoImpl();
		ICountryDao countryDao = service.getCountryDaoImpl();
		IShoppingCartDao scDao = service.getShoppingCartDaoImpl();
		IAddressDao addrDao = service.getAddressDaoImpl();

		ICustomer customer = null;
		if ( returningFlag.equals( "Y" ) )
		{
			String uname = request.getParameter( "username" );
			String passwd = request.getParameter( "password" );

			if ( uname.length() == 0 || passwd.length() == 0 )
			{
				errors.add( "Username and password cannot be empty!" );
			}
			else
			{
				customer = customerDao.getUserBy( uname, passwd );
				if ( customer == null )
				{
					errors.add( "Invalid username or password!" );
				}

//				if ( errors.isEmpty() )
//				{
//					session.setAttribute( "customer", customer );
//				}
			}
		}
		else if ( returningFlag.equals( "N" ) )
		{
			String fname = request.getParameter( "fname" );
			checkEmptiness( fname, "First name", errors );
			String lname = request.getParameter( "lname" );
			checkEmptiness( lname, "Last name", errors );
			String city = request.getParameter( "city" );
			String state = request.getParameter( "state" );
			String street1 = request.getParameter( "street1" );
			String street2 = request.getParameter( "street2" );
			String zip = request.getParameter( "zip" );
			String country = request.getParameter( "country" );
			checkEmptiness( country, "Country", errors );
			String phone = request.getParameter( "phone" );
			String email = request.getParameter( "email" );
			String birthdate = request.getParameter( "birthdate" );
			String data = request.getParameter( "data" );

			if ( errors.isEmpty() )
			{
				customer = customerDao.getObject();
				customer.setCFname( fname );
				customer.setCLname( lname );

				IAddress address = addrDao.getObject();
				address.setAddrCity( city );
				address.setAddrState( state );
				address.setAddrStreet1( street1 );
				address.setAddrStreet2( street2 );
				address.setAddrZip( zip );
				


				ICountry countryObj = countryDao.getByName( country );
				if ( countryObj == null )
				{
					errors.add( "Country " + country + " doesn't exist" );
				}
				else
				{
					address.setCountry( countryObj );
				}
				
				address.getCustomers().add( customer );
				addrDao.shrani( address );
				
				customer.setAddress( address );
				customer.setCPhone( phone );
				SimpleDateFormat sdf = new SimpleDateFormat( "dd/mm/yyyy" );

				try
				{
					customer.setCBirthdate( sdf.parse( birthdate ) );
				}
				catch ( ParseException e )
				{
					e.printStackTrace();
				}
				customer.setCData( data );
				customer.setCEmail( email );
				customer.setCBalance( 0.0 );
				customer.setCDiscount( (int) ( java.lang.Math.random() * 51 ) );
				customer.setCExpiration( new Date(
				        System.currentTimeMillis() + 7200000 ) );
				customer.setCLastVisit( new Date( System.currentTimeMillis() ) );
				customer.setCLogin( new Date( System.currentTimeMillis() ) );
				customer.setCSince( new Date( System.currentTimeMillis() ) );
				customer.setCYtdPmt( 0.0 );
				customer.setCUname( "" );
				customer.setCPasswd( "" );
				customerDao.shrani( customer );
				customer.setCUname( DigSyl( customer.getCId(), 0 ).toLowerCase() );
				customer.setCPasswd( customer.getCUname().toLowerCase() );
				customerDao.shrani( customer ); // update it with username and
												// password
			}
		}
		else
		{
			errors.add( "RETURNING_FLAG not set to Y or N!" );
		}

		if ( shoppingId == null )
		{
			errors.add( "Shopping cart ID not set!" );
		}
		
		IShoppingCart cart = scDao.findById( shoppingId );
		model.addAttribute( "errors", errors );
		model.addAttribute( "cart", cart );
		model.addAttribute( "customer", customer );
		
//		if( cart != null )
//			model.addAttribute( "subTotal", getSubTotal( cart.getShoppingCartLines() ) );

		setupFrontend(model, shoppingId, customerId);
		return "buy";
	}

	private double getSubTotal(Set<IShoppingCartLine> shoppingCartLines)
	{
		double total = 0;
		for ( IShoppingCartLine scl : shoppingCartLines )
		{
			total += scl.getItem().getICost();
		}

		return total;
	}

	private void checkEmptiness(String obj, String desc,
	        ArrayList<String> errors)
	{
		if ( obj == null || obj.isEmpty() )
		{
			errors.add( desc + " is empty!" );
		}
	}

	private static final String[] digS = {"BA", "OG", "AL", "RI", "RE", "SE",
	        "AT", "UL", "IN", "NG"    };

	public String DigSyl(int d, int n)
	{
		String s = "";

		if ( n == 0 )
			return ( DigSyl( d ) );
		for ( ; n > 0; n-- )
		{
			int c = d % 10;
			s = digS[c] + s;
			d = d / 10;
		}

		return ( s );
	}

	private String DigSyl(int d)
	{
		String s = "";

		for ( ; d != 0; d = d / 10 )
		{
			int c = d % 10;
			s = digS[c] + s;
		}

		return ( s );
	}
}
