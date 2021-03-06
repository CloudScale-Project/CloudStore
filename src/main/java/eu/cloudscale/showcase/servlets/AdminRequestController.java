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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.cloudscale.showcase.db.model.IItem;

@Controller
@RequestMapping("/admin")
public class AdminRequestController extends AController 
{
	@RequestMapping(value="", method=RequestMethod.GET)
	public String get(@RequestParam( value="I_ID", required=false) Integer itemId, HttpServletRequest request, Model model)
	{
		HttpSession session = super.getHttpSession(BestSellersController.class, request);
		
		if ( itemId != null)
		{
			IItem item = service.findItemById(itemId);
			model.addAttribute("item", item);	
		}
		else
		{
			model.addAttribute("error", "No item with that ID!");
		}
		
		setupFrontend( model, null, null );
		return "admin";
	}
}
