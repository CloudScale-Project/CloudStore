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
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.cloudscale.showcase.db.common.DatabaseHelper;


@Controller
@RequestMapping( "/" )
public class HomeController extends AController
{
	static Logger log = LogManager.getLogger(HomeController.class.getName());
	
	private String[]            categories = {"ARTS", "NON-FICTION",
	        "BIOGRAPHIES", "PARENTING", "BUSINESS", "POLITICS", "CHILDREN",
	        "REFERENCE", "COMPUTERS", "RELIGION", "COOKING", "ROMANCE",
	        "HEALTH", "SELF-HELP", "HISTORY", "SCIENCE-NATURE", "HOME",
	        "SCIENCE-FICTION", "HUMOR", "SPORTS", "LITERATURE", "MYSTERY" };


	private class Split
	{

		public String left, right, leftUrl, rightUrl;

		public Split(String l, String lUrl, String r, String rUrl)
		{
			left = l;
			leftUrl = lUrl;
			rightUrl = rUrl;
			right = r;
		}
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping( value = "/", method = RequestMethod.GET )
	public String home(
	        @RequestParam( value = "SHOPPING_ID", required = false ) Integer shoppingId,
	        @RequestParam( value = "C_ID", required = false ) Integer customerId,
	        Locale locale, HttpServletRequest request, Model model)
	{
		HttpSession session = super.getHttpSession(HomeController.class, request);
	
		String categoryUrl = getUrl1( shoppingId, customerId, "" );
		
		model.addAttribute( "promotional", service.getPromotional() );
		model.addAttribute( "categories", prepareCategories( categoryUrl ) );

		model.addAttribute( "categoryUrl", categoryUrl );


		String productUrl = getProductUrl( shoppingId, customerId );
		model.addAttribute( "productUrl", productUrl );
		setupFrontend( model, shoppingId, customerId );

		return "home";
	}


	private String getProductUrl(Integer shoppingId, Integer customerId)
	{
		return getUrl1( shoppingId, customerId, "" );
	}

	// private String getCategoryUrl(Integer shoppingId, Integer customerId)
	// {
	// String url = "";
	// if( shoppingId != null)
	// url+="&SHOPPING_ID=" + shoppingId;
	//
	// if( customerId != null )
	// url += "&C_ID=" + customerId;
	//
	// return url;
	// }

	private List<Split> prepareCategories(String url)
	{
		ArrayList<Split> cats = new ArrayList<Split>();

		for ( int i = 0; i < categories.length; i += 2 )
		{
			cats.add( new Split( categories[i], categories[i] + url,
			        categories[i + 1], categories[i + 1] + url ) );
		}
		return cats;
	}
}
