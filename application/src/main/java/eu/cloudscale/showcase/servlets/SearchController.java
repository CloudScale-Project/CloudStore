package eu.cloudscale.showcase.servlets;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.cloudscale.showcase.db.common.DatabaseHelper;

@Controller
@RequestMapping("/search")
public class SearchController extends AController
{
	
	@RequestMapping( method=RequestMethod.GET )
	public String get(HttpServletRequest request, Model model)
	{
	
		HttpSession session = request.getSession(false);
		
		String field = request.getParameter("searchField");
		String keyword = request.getParameter( "keyword" );
		ArrayList<String> errors = new ArrayList<String>();
		
		if( keyword == null)
		{
			keyword = "";
			errors.add( "Search string is empty" );
			model.addAttribute( "errors", errors );
		}
		
		if( errors.isEmpty() )
		{
    		if( field.equalsIgnoreCase( "author" ))
    		{
    			model.addAttribute( "results", service.searchByAuthor(keyword) );
    		}
    		else if( field.equalsIgnoreCase( "title" ))
    		{
    			model.addAttribute( "results", service.searchByTitle(keyword) );
    		}
    		else if( field.equalsIgnoreCase( "subject" ))
    		{
    			model.addAttribute( "results", service.searchBySubject(keyword) );
    		}
		}
		
		model.addAttribute( "searchField", field );
		model.addAttribute( "keyword", keyword );
		setupUrls( model, request);
		return "search";
	}
	
	private void setupUrls(Model model, HttpServletRequest request)
	{
		Integer shoppingId = null;
		if( request.getParameter( "SHOPPING_ID" ) != null)
		{
			shoppingId = Integer.valueOf(request.getParameter( "SHOPPING_ID" ));
		}
		
		Integer customerId = null;
		if( request.getParameter( "C_ID" ) != null)
		{
			shoppingId = Integer.valueOf(request.getParameter( "C_ID" ));
		}
		setupFrontend( model, shoppingId, customerId);
		
		String productUrl = getProductUrl(shoppingId, customerId);
		model.addAttribute( "productUrl", productUrl);
	}
	

	private String getProductUrl(Integer shoppingId, Integer customerId)
    {
	    return getUrl2(shoppingId, customerId, "");
    }
}
