/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/
package eu.cloudscale.showcase.servlets;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.cloudscale.showcase.db.dao.IItemDao;
import eu.cloudscale.showcase.db.model.IItem;

@Controller
@RequestMapping("/product-detail")
public class ProductDetailServlet extends AController
{
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
					@RequestParam(value= "I_ID", required=false) Integer itemId,
					@RequestParam(value = "C_ID", required=false ) Integer customerId,
					@RequestParam(value = "SHOPPING_ID", required=false) Integer shoppingId,
					HttpServletRequest request,
					Locale locale, 
					Model model)
	{
		HttpSession session = super.getHttpSession(ProductDetailServlet.class, request);
				

		IItem item = service.findItemById(itemId);
		
		String addToShoppingCartUrl = buildAddToShoppingCartUrl(shoppingId, customerId, itemId);
		model.addAttribute( "addToShoppingCartUrl", addToShoppingCartUrl);
		
		String adminUrl = buildAdminUrl(shoppingId, customerId, itemId);
		model.addAttribute( "adminUrl", adminUrl );
		
		model.addAttribute( "item", item);
		setupFrontend( model, shoppingId, customerId );
		
		return "product_detail";
	}

	private String buildAddToShoppingCartUrl(Integer shoppingId, Integer customerId, Integer itemId)
    {
		String url = getUrl2(shoppingId, customerId, "/shopping-cart");
		if( url.equals( "/shopping-cart" ))
			url += "?";
		
		url += "&I_ID=" + itemId + "&QTY=1&ADD_FLAG=Y";
		return url;
    }

	private String buildAdminUrl(Integer shoppingId, Integer customerId, int id)
    {
	    return getUrl2(shoppingId, customerId, "/admin");
    }

	
	
}
