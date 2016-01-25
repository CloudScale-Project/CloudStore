/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.servlets;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.cloudscale.showcase.db.dao.ICustomerDao;
import eu.cloudscale.showcase.db.model.ICustomer;

@Controller
@RequestMapping("/login")
public class LoginController extends AController
{
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public String get(HttpServletRequest request, HttpSession session, Model model)
	{		
		String referer = request.getParameter("next");
		if( referer.isEmpty() )
			referer = request.getHeader( "referer" );
		
		if( session.getAttribute( "customer" ) != null)
			return "redirect:" + referer == null ? "/" : referer;
		
		model.addAttribute("referer", referer);
		return "login";
	}
	
	@RequestMapping(value="", method=RequestMethod.POST)
	public String post(@RequestParam("username") String username, 
						@RequestParam("password") String password,
						@RequestParam("referer") String referer,
						HttpSession session, Model model)
	{
		ICustomer customer = null;
		
		if( (customer = service.getUserBy(username, password)) != null )
		{
			Date currDate = new Date(); 
			
			
			Calendar c = Calendar.getInstance();
			c.setTime( currDate );
			c.add( Calendar.HOUR, -1 );
			
			customer.setCLogin( currDate );
			customer.setCExpiration( c.getTime() );
			service.saveCustomer(customer);
			session.setAttribute( "customer", customer );
			return "redirect:" + referer;
		}
		
		model.addAttribute("errors", "Login failed. Check username and password");
		return "login";
	}
}
