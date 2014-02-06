package eu.cloudscale.showcase.servlets;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.cloudscale.showcase.db.dao.IShoppingCartDao;
import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;

@Controller
@RequestMapping("/shopping-cart")
public class ShoppingCartController extends AController
{
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public String get(HttpServletRequest request, Model model)
	{
		HttpSession session = request.getSession(false);
				
		IShoppingCartDao scDao = service.getShoppingCartDaoImpl();
		
		ArrayList<String> errors = new ArrayList<String>();
		Integer itemId = null;
		Integer customerId = null;
		Integer shoppingId = null;
		if( request.getParameter( "I_ID" ) != null )
		{
			itemId = Integer.parseInt(request.getParameter( "I_ID" ));
		}
		
		if( request.getParameter( "C_ID" ) != null )
		{
			customerId = Integer.parseInt(request.getParameter( "C_ID" ));
		}
		
		if( request.getParameter( "SHOPPING_ID" ) != null )
		{
			shoppingId = Integer.parseInt(request.getParameter( "SHOPPING_ID" ));
		}
		
		String addFlag = request.getParameter("ADD_FLAG");
		
		if( shoppingId == null || scDao.findById( shoppingId ) == null )
		{
			shoppingId = scDao.createEmptyCart();
		}
		
		if( addFlag != null && addFlag.equals( "Y" ) )
		{
			if(itemId == null)
				errors.add("No item id!");
		}
		
		if( errors.isEmpty() )
		{
    		List<Integer> quantities = new ArrayList<Integer>();
    		List<Integer> ids = new ArrayList<Integer>();
    		int i = 0;
    		String curr_QTYstr = request.getParameter( "QTY_" + i ) ;
    		String curr_I_IDstr = request.getParameter( "I_ID_" + i );
    		
    		while (curr_I_IDstr != null) 
    		{
    			ids.add(Integer.parseInt(curr_I_IDstr));
    			quantities.add(Integer.parseInt((curr_QTYstr)));
    			i++;
    			curr_QTYstr = request.getParameter("QTY_" + i);
    			curr_I_IDstr = request.getParameter("I_ID_" + i);
    		}
    		
    		IShoppingCart cart = service.doCart(shoppingId, itemId, ids, quantities);
    		model.addAttribute( "cart", cart);
//    		model.addAttribute( "subTotal", getSubTotal(cart.getShoppingCartLines()));
		
    		String customerRegistration = getCustomerRegistrationURL(customerId, shoppingId);
    		model.addAttribute( "checkoutUrl", customerRegistration);
		
    		if( customerId != null )
    			model.addAttribute("customerId", customerId);
		}
		
		model.addAttribute("errors", errors);
		setupFrontend(model, shoppingId, customerId);
		return "shopping-cart";
	}

	private double getSubTotal(Set<IShoppingCartLine> shoppingCartLines)
    {
		double subtotal = 0;
		for(IShoppingCartLine scl : shoppingCartLines)
		{
			subtotal += scl.getItem().getICost();
		}
		
		return subtotal;
    }


	private String getCustomerRegistrationURL(Integer customerId, Integer shoppingId)
    {
		return getUrl2(shoppingId, customerId, "/customer-registration");
    }	
	
}
