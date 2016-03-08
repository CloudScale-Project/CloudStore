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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping( "/order-inquiry" )
public class OrderInquiryController extends AController
{

	@RequestMapping( method = RequestMethod.GET )
	public String get(HttpServletRequest request, Model model)
	{
		HttpSession session = super.getHttpSession(OrderInquiryController.class, request);

		if ( session == null )
			session = request.getSession( true );

		Integer C_ID = null;
		try
		{
			C_ID = Integer.parseInt(request.getParameter( "C_ID" ));
		}
		catch(Exception e)
		{
		}
		
		Integer SHOPPING_ID = null;
		try
		{
			SHOPPING_ID = Integer.parseInt(request.getParameter( "SHOPPING_ID" ));
		}
		catch(Exception e)
		{
		}

		if ( C_ID != null )
		{
			model.addAttribute( "customerId", C_ID );
		}

		if ( SHOPPING_ID != null )
		{
			model.addAttribute( "shoppingId", SHOPPING_ID );
		}

		setupFrontend( model, SHOPPING_ID, C_ID );
		return "order-inquiry";
	}

}
