package eu.cloudscale.showcase.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.cloudscale.showcase.db.model.ICustomer;


@Controller
@RequestMapping( "/customer-registration" )
public class CustomerRegistrationController extends AController
{

	@RequestMapping( "" )
	public String get(
	        @RequestParam( value = "C_ID", required = false ) Integer customerId,
	        @RequestParam( value = "SHOPPING_ID", required = false ) Integer shoppingId,
	        HttpServletRequest request, Model model)
	{
		HttpSession session = request.getSession( false );

		if ( session == null )
			session = request.getSession( true );

		ICustomer customer = null;
		if ( customerId != null )
		{
			customer = service.getCustomerDaoImpl().findById( customerId );
		}

		model.addAttribute( "shoppingId", shoppingId );
		model.addAttribute( "customerId", customerId );

		String shoppingCartUrl = getShoppingCartUrl( shoppingId, customerId );
		model.addAttribute( "shoppingCartUrl", shoppingCartUrl );

		String searchUrl = getSearchRequestUrl( shoppingId, customerId );
		model.addAttribute( "searchUrl", searchUrl );

		String homeUrl = getHomeUrl( shoppingId, customerId );
		model.addAttribute( "homeUrl", homeUrl );

		model.addAttribute( "sessionId", session.getId() );

		setupFrontend( model, shoppingId, customerId );
		return "customer-registration";
	}
}
