package eu.cloudscale.showcase.servlets;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.cloudscale.showcase.db.model.ICustomer;

@Controller
@RequestMapping( "/" )
public class HomeController extends AController
{

	private String[]            categories = {"ARTS", "NON-FICTION",
	        "BIOGRAPHIES", "PARENTING", "BUSINESS", "POLITICS", "CHILDREN",
	        "REFERENCE", "COMPUTERS", "RELIGION", "COOKING", "ROMANCE",
	        "HEALTH", "SELF-HELP", "HISTORY", "SCIENCE-NATURE", "HOME",
	        "SCIENCE-FICTION", "HUMOR", "SPORTS", "LITERATURE", "MYSTERY" };


	private class Split
	{

		public String left, right, leftUrl, rightUrl;

		public Split(String l, String lUrl, String r, String rUrl)
		{
			left = l;
			leftUrl = lUrl;
			rightUrl = rUrl;
			right = r;
		}
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping( value = "/", method = RequestMethod.GET )
	public String home(
	        @RequestParam( value = "SHOPPING_ID", required = false ) Integer shoppingId,
	        @RequestParam( value = "C_ID", required = false ) Integer customerId,
	        Locale locale, HttpServletRequest request, Model model)
	{
		HttpSession session = request.getSession( false );

		if ( session == null )
			session = request.getSession( true );

		String categoryUrl = getUrl1( shoppingId, customerId, "" );

		model.addAttribute( "promotional", getPromotional() );
		model.addAttribute( "categories", prepareCategories( categoryUrl ) );

		model.addAttribute( "categoryUrl", categoryUrl );

		setupFrontend( model, shoppingId, customerId );

		String productUrl = getProductUrl( shoppingId, customerId );
		model.addAttribute( "productUrl", productUrl );

		return "home";
	}


	private String getProductUrl(Integer shoppingId, Integer customerId)
	{
		return getUrl1( shoppingId, customerId, "" );
	}

	// private String getCategoryUrl(Integer shoppingId, Integer customerId)
	// {
	// String url = "";
	// if( shoppingId != null)
	// url+="&SHOPPING_ID=" + shoppingId;
	//
	// if( customerId != null )
	// url += "&C_ID=" + customerId;
	//
	// return url;
	// }

	private List<Split> prepareCategories(String url)
	{
		ArrayList<Split> cats = new ArrayList<Split>();

		for ( int i = 0; i < categories.length; i += 2 )
		{
			cats.add( new Split( categories[i], categories[i] + url,
			        categories[i + 1], categories[i + 1] + url ) );
		}
		return cats;
	}
}
