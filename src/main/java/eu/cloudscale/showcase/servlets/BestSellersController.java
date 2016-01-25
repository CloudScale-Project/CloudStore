/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/best-sellers")
public class BestSellersController extends AController
{
	static final Logger log = LogManager.getLogger(BestSellersController.class.getName());
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public String bestSellers(@RequestParam( value="SUBJECT", required=false) String category, 
							  @RequestParam( value="C_ID", required=false) Integer customerId,
							  @RequestParam( value="SHOPPING_ID", required=false ) Integer shoppingId,
							  HttpServletRequest request, Model model)
	{
		HttpSession session = super.getHttpSession(BestSellersController.class, request);

		List<Object[]> res = service.getBestSellers( category );
		model.addAttribute( "products", res );
		
		String productUrl = getProductUrl(shoppingId, customerId);
		model.addAttribute( "productUrl", productUrl);
		model.addAttribute( "promotional", service.getPromotional() );	
		setupFrontend( model, shoppingId, customerId );
		
		return "best-sellers";
	}
	
	private String getProductUrl(Integer shoppingId, Integer customerId)
    {
	    return getUrl2(shoppingId, customerId, "");
    }
}
