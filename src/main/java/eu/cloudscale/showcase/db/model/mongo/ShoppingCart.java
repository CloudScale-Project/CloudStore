/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.db.model.mongo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import eu.cloudscale.showcase.db.common.DatabaseHelper;
import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;
import eu.cloudscale.showcase.db.services.IService;
import eu.cloudscale.showcase.db.services.MongoService;

@Component
@Document( collection = "shoppingCart" )
public class ShoppingCart implements IShoppingCart, Serializable
{
//
//	@Autowired
//	@Qualifier("service")
//	private IService service;
	
    private static final long serialVersionUID = -235081098185134853L;

	@Id
	private ObjectId               id;

	@Indexed
	private Integer                scId;

	private Date                   scTime;
	
	

	public ShoppingCart()
	{
	}

	public ShoppingCart(Date scTime)
	{
		this.scTime = scTime;
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
	public Integer getScId()
	{
		return this.scId;
	}

	@Override
	public void setScId(Integer scId)
	{
		this.scId = scId;
	}

	@Override
	public Date getScTime()
	{
		return this.scTime;
	}

	@Override
	public void setScTime(Date scTime)
	{
		this.scTime = scTime;
	}

	@Override
    public Set<IShoppingCartLine> getShoppingCartLines()
    {
		List  res = DatabaseHelper.getDatabase().findAllShoppingCartLinesBySC( this );
		if( res == null || res.isEmpty() )
			return new HashSet<IShoppingCartLine>();
		
		return new HashSet<IShoppingCartLine>(res);
    }
}
