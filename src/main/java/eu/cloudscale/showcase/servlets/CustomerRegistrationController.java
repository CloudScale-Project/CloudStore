/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
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
		HttpSession session = super.getHttpSession(CustomerRegistrationController.class, request);
		
		ICustomer customer = null;
		if ( customerId != null )
		{
			customer = service.findCustomerById( customerId );
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
