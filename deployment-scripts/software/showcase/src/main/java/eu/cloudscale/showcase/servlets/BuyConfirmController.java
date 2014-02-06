package eu.cloudscale.showcase.servlets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.cloudscale.showcase.db.BuyConfirmResult;
import eu.cloudscale.showcase.db.dao.ICountryDao;
import eu.cloudscale.showcase.db.model.ICountry;

@Controller
@RequestMapping("/buy-confirm")
public class BuyConfirmController extends AController
{
	@RequestMapping(value="", method=RequestMethod.GET)
	public String get(HttpServletRequest request,
						Model model)
	{
		HttpSession session = request.getSession(false);
				
		ArrayList<String> errors = new ArrayList<String>();
		
		String shoppingIdString = request.getParameter( "SHOPPING_ID" );
		Integer shoppingId = null;
		if( shoppingIdString != null && !shoppingIdString.isEmpty() )
		{
			shoppingId = Integer.parseInt(  shoppingIdString );
		}
		
		String customerIdString = request.getParameter( "C_ID" );
		Integer customerId = null;
		if( customerIdString != null && !customerIdString.isEmpty() )
		{
			customerId = Integer.parseInt(  customerIdString );
		}
		
		String ccType = request.getParameter( "CC_TYPE" );
		String ccNumber_str = request.getParameter( "CC_NUMBER" );
		Long ccNumber = null;
		if( !ccNumber_str.isEmpty() )
			ccNumber = Long.parseLong( ccNumber_str );
		
		SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
		String ccName = request.getParameter( "CC_NAME" );
		Date ccExpiry = null;
        try
        {
	        ccExpiry = sdf.parse(request.getParameter( "CC_EXPIRY" ));
        }
        catch ( ParseException e )
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		String shipping = request.getParameter( "SHIPPING" );
		String street1 = request.getParameter( "street1" );
		
		
		BuyConfirmResult res = null;
		if( street1 != null && street1.equals( "" ))
		{
			checkEmptiness(errors, shoppingId, customerId, ccType, ccNumber, ccName, ccExpiry, shipping);
			if( errors.isEmpty() )
				res = service.doBuyConfirm( shoppingId, customerId, ccType, ccNumber, ccName, ccExpiry, shipping);
		}
		else
		{
			String street2 = request.getParameter("street2");
			String city = request.getParameter("city");
			String state = request.getParameter( "state" );
			String zip = request.getParameter("zip");
			String country = request.getParameter("country");
			checkEmptiness(errors, shoppingId, customerId, ccType, ccNumber, ccName, ccExpiry, shipping, street1, country, city, state, zip);
			if( errors.isEmpty() )
				res = service.doBuyConfirm(shoppingId, customerId, ccType, ccNumber, ccName, ccExpiry, shipping, street1, street2, city, state, zip, country);
		}
		
		model.addAttribute("errors", errors);
		model.addAttribute("results", res);
		setupFrontend( model, shoppingId, customerId );
		return "buy-confirm";
	}

	private void checkEmptiness(ArrayList<String> errors, Integer shoppingId,
            Integer customerId, String ccType, Long ccNumber, String ccName,
            Date ccExpiry, String shipping)
    {
		if(shoppingId == null)
	    	errors.add( "Shipping id is null!" );
	    if( customerId == null)
	    	errors.add( "Customer id is null" );
	    if( ccType == null || ccType.isEmpty() )
	    	errors.add( "ccType is null" );
	    if( ccNumber == null)
	    	errors.add("ccNumber is null");
	    if( ccName == null || ccName.isEmpty() )
	    	errors.add( "ccName is null" );
	    if( ccExpiry == null)
	    	errors.add( "ccExpiry is null" );
	    if( shipping == null || shipping.isEmpty() )
	    	errors.add( "Shipping is null" );
    }

	private boolean countryExist( String country)
    {
		ICountryDao countryDao = service.getCountryDaoImpl();
		
		ICountry country1 = countryDao.getByName( country );
		return country1 == null;
    }

	private void checkEmptiness(ArrayList<String> errors, Integer shoppingId,
            Integer customerId, String ccType, Long ccNumber, String ccName,
            Date ccExpiry, String shipping, String street, String country, String city, String state, String zip)
    {
	    checkEmptiness( errors, shoppingId, customerId, ccType, ccNumber, ccName, ccExpiry, shipping );
	    
	    if( city == null )
	    	errors.add( "City is null" );
	    
	    if( state == null )
	    	errors.add( "State is null" );
	    
	    if( zip == null )
	    	errors.add( "Zip is null" );
	    
	    if( street == null )
	    	errors.add( "Street1 or street2 is null" );
	    
	    if( !countryExist(country) )
	    	errors.add( "That country doesn't exist!" );
    }

}
