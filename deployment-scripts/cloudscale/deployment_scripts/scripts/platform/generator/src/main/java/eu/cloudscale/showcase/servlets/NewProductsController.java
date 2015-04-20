package eu.cloudscale.showcase.servlets;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping( "/new-products" )
public class NewProductsController extends AController
{

//	@RequestMapping( value = "/", method = RequestMethod.GET )
//	public String newProducts(Locale locale, Model model)
//	{
//		return "new-products";
//	}

	@RequestMapping( value = "", method = RequestMethod.GET )
	public String newProductsCategory(
	        @RequestParam( value="SUBJECT", required=false) String category,
	        @RequestParam( value="C_ID", required=false) Integer customerId,
	        @RequestParam( value="SHOPPING_ID", required=false ) Integer shoppingId,
	        Locale locale,
	        HttpServletRequest request,
	        Model model)
	{
		HttpSession session = request.getSession(false);
		
		return getNewProducts(category, model, customerId, shoppingId );
	}
	
//	@RequestMapping( value = "/", method = RequestMethod.GET )
//	public String newProducts(@PathVariable("category") String category, 
//								@PathVariable("customerId") Integer customerId,
//								Model model)
//	{
//		
//		return getNewProducts( category, model, customerId, null);
//	}
//	
//	@RequestMapping( value = "/{category}/{customerId}/{shoppingId}", method = RequestMethod.GET )
//	public String newProducts(@PathVariable("category") String category, 
//								@PathVariable("customerId") Integer customerId,
//								@PathVariable("shoppingId") Integer shoppingId,
//								Model model)
//	{
//		
//		return getNewProducts( category, model, customerId, shoppingId);
//	}

	private String getNewProducts(String category, Model model, Integer customerId, Integer shoppingId)
	{
		
		String productUrl = getProductUrl(shoppingId, customerId);
		model.addAttribute( "productUrl", productUrl);
		
		model.addAttribute( "products", service.getNewProducts( category ) );
		model.addAttribute( "promotional", getPromotional() );
		
		setupFrontend(model, shoppingId, customerId);
		return "new-products";
	}

	private String getProductUrl(Integer shoppingId, Integer customerId)
    {
	    return getUrl1(shoppingId, customerId, "");
    }

}
