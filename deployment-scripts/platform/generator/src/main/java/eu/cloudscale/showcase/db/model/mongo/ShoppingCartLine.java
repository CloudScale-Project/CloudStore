package eu.cloudscale.showcase.db.model.mongo;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import eu.cloudscale.showcase.db.IService;
import eu.cloudscale.showcase.db.common.DatabaseHelper;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;
import eu.cloudscale.showcase.db.services.MongoService;

@Component
@Document( collection = "shoppingCartLine" )
public class ShoppingCartLine implements IShoppingCartLine, Serializable
{
	@Autowired
	@Qualifier("service")
	private IService service;
	
    private static final long serialVersionUID = 767045854888711002L;

	@Id
	private ObjectId id;
	
	private Integer sclId;

	private Integer item;

	private Integer sclQty;
	
	private Integer shoppingCart;
	
	public ShoppingCartLine()
	{
	}

	
    public ObjectId getId()
    {
    	return id;
    }

    public void setId(ObjectId id)
    {
    	this.id = id;
    }

	@Override
	public IShoppingCart getShoppingCart()
    {
    	return DatabaseHelper.getDatabase().getShoppingCartDaoImpl().findById( shoppingCart );
    }

	@Override
    public void setShoppingCart(IShoppingCart shoppingCart)
    {
    	this.shoppingCart = shoppingCart.getScId();
    }

	@Override
	public Integer getSclId()
	{
		return this.sclId;
	}

	@Override
	public void setSclId(Integer sclScId)
	{
		this.sclId = sclScId;
	}

	@Override
	public IItem getItem()
	{
		IItem item = DatabaseHelper.getDatabase().getItemDaoImpl().findById( this.item );
		return item;
	}

	@Override
	public void setItem(IItem item)
	{
		this.item = item.getIId();
	}

	@Override
	public Integer getSclQty()
	{
		return this.sclQty;
	}

	@Override
	public void setSclQty(Integer sclQty)
	{
		this.sclQty = sclQty;
	}
}
