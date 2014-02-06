package eu.cloudscale.showcase.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.ui.Model;
import org.springframework.util.PropertyPlaceholderHelper;

import eu.cloudscale.showcase.db.IService;
import eu.cloudscale.showcase.db.dao.IItemDao;
import eu.cloudscale.showcase.db.model.IItem;

public abstract class AController 
{
	@Autowired
	@Qualifier("service")
    protected IService service;

	public List<IItem> getPromotional() 
	{
		IItemDao itemDao = service.getItemDaoImpl();
		List<IItem> promotional = itemDao.getPromotional();

		return promotional;
	}
	
	protected String getUrl2(Integer shoppingId, Integer customerId, String url1)
    {
		String url = new String(url1);
		if( shoppingId != null)
	    {
	    	url += "?SHOPPING_ID=" + shoppingId;
	    	if( customerId != null )
	    	{
	    		url += "&C_ID=" + customerId;
	    	}
	    }
	    else if ( customerId != null )
	    {
	    	url += "?C_ID=" + customerId;
	    }
		
		return url;
    }

	protected String getUrl1(Integer shoppingId, Integer customerId, String url1)
    {
		String url = new String(url1);
		if( shoppingId != null )
		{
			url += "&SHOPPING_ID=" + shoppingId;
		}
		
		if( customerId != null )
		{
			url += "&C_ID=" + customerId;
		}
		
		return url;
    }	
	
	protected String getShoppingCartUrl(Integer shoppingId, Integer customerId)
	{
		return getUrl2( shoppingId, customerId, "/shopping-cart" );
	}

	protected String getHomeUrl(Integer shoppingId, Integer customerId)
	{
		return getUrl2( shoppingId, customerId, "/" );
	}

	protected String getSearchRequestUrl(Integer shoppingId, Integer customerId)
	{
		return getUrl2( shoppingId, customerId, "/search" );
	}
	
	protected String getOrderInquiryUrl(Integer shoppingId, Integer customerId)
    {
	    return getUrl2( shoppingId, customerId, "/order-inquiry" );
    }
	
	protected void setupFrontend(Model model, Integer shoppingId, Integer customerId)
	{
		String shoppingCartUrl = getShoppingCartUrl( shoppingId, customerId );
		model.addAttribute( "shoppingCartUrl", shoppingCartUrl );

		String searchUrl = getSearchRequestUrl( shoppingId, customerId );
		model.addAttribute( "searchUrl", searchUrl );

		String homeUrl = getHomeUrl( shoppingId, customerId );
		model.addAttribute( "homeUrl", homeUrl );
		
		String orderInquiryUrl = getOrderInquiryUrl(shoppingId, customerId);
		model.addAttribute( "orderInquiryUrl", orderInquiryUrl );
		
		model.addAttribute( "cssResourceUrl", getApplicationProperties().get( "eu.cloudscale.files.url.css" ));
		model.addAttribute( "imgResourceUrl", getApplicationProperties().get( "eu.cloudscale.files.url.img" ));
		model.addAttribute( "jsResourceUrl", getApplicationProperties().get( "eu.cloudscale.files.url.js" ));
	}

	protected Properties getApplicationProperties()
	{
		Resource resource = new ClassPathResource("app.properties");
		Properties props = null;
		try
        {
	        props = PropertiesLoaderUtils.loadProperties(resource);
        }
        catch ( IOException e )
        {
	        e.printStackTrace();
        }
		
		return props;
	}
}
