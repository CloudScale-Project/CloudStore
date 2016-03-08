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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;

@Controller
@RequestMapping("/order-display")
public class OrderDisplayController extends AController
{
	@SuppressWarnings( {"unchecked", "rawtypes" } )
    @RequestMapping(method=RequestMethod.GET)
	public String get(HttpServletRequest request, Model model)
	{
		HttpSession session = super.getHttpSession(OrderDisplayController.class, request);
		
		if( session == null )
			session = request.getSession(true);
		
		Integer customerId = null;
		try
		{
			customerId = Integer.parseInt( request.getParameter("C_ID") );
		}
		catch(Exception e) {}
		
		Integer shoppingId = null;
		try
		{
			shoppingId = Integer.parseInt( request.getParameter("SHOPPING_ID") );
		}
		catch(Exception e) {}
		
		String uname = request.getParameter("username");
		String passwd = request.getParameter("password");
		
		ArrayList<String> errors = new ArrayList<String>();
		
		IOrders order = null;
		List<IOrderLine> orderLines = null;
		
		if (uname != null && passwd != null) 
		{

			ICustomer customer = service.getUserBy( uname, passwd);
			
			if (customer == null) 
			{
				errors.add( "Error: Incorrect password or username.\n" );
			} 
			else 
			{
				order = service.getMostRecentOrder( customer );
				if (order == null)
				{
					errors.add("User doesn't have orders");
				}
				else 
				{
					orderLines = service.findAllOrderLineByOrder( order );
				}
			}

		} 
		else
		{
			errors.add("Error: TPCW_order_display_servlet, uname and passwd not set!.\n");
		}
		
		if( errors.isEmpty() )
		{
			model.addAttribute( "order", order );
			model.addAttribute( "orderLines", orderLines );
		}
		
		model.addAttribute( "errors", errors);
		
		setupFrontend( model, shoppingId, customerId );
		return "order-display";
	}
}
