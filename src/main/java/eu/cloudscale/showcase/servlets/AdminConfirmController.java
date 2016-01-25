/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.servlets;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eu.cloudscale.showcase.db.model.IItem;

@Controller
@RequestMapping("/admin-confirm")
public class AdminConfirmController extends AController 
{
	@RequestMapping(value="", method=RequestMethod.POST)
	public String post(@RequestParam(value="I_ID") Integer itemId,
						@RequestParam(value = "new_price", required=false) Double newPrice,
						@RequestParam(value = "new_picture", required=false) String newPicture,
						@RequestParam(value = "new_thumbnail", required=false) String newThumbnail,
						HttpServletRequest request, Model model, RedirectAttributes redirectAttributes)
	{
		IItem item = service.findItemById(itemId);
		if (newPrice != null)
			item.setICost(newPrice);
		if (newPicture != null && newPicture != "")
			item.setIImage(newPicture);
		if (newThumbnail != null && newThumbnail != "")
			item.setIThumbnail(newThumbnail);
		service.saveItem(item);
		
		setupFrontend( model, null, null );
		redirectAttributes.addAttribute("I_ID", itemId);
		return "redirect:/product-detail";
	}
}
