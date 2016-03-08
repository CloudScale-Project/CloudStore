/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/
package eu.cloudscale.showcase.db.model.mongo;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import eu.cloudscale.showcase.db.common.DatabaseHelper;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.services.IService;
import eu.cloudscale.showcase.db.services.MongoService;

@Component
@Document( collection = "orderLine")
public class OrderLine implements IOrderLine, Serializable
{
//	@Autowired
//	@Qualifier("service")
//
//	private MongoService service;
	
    private static final long serialVersionUID = -6695288937139715783L;

	@Id
	private ObjectId id;
	
	@Indexed
	private Integer    olId;

	private Integer orderId;

	private Integer itemId;

	private Integer olQty;

	private Double olDiscount;

	private String  olComment;
	

	public OrderLine()
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
	public Integer getOlId()
	{
		return this.olId;
	}

	@Override
	public void setOlId(Integer olId)
	{
		this.olId = olId;
	}
	
	@Override
	public IOrders getOrders()
	{
		return DatabaseHelper.getDatabase().findOrdersById( orderId );
	}

	@Override
	public void setOrders(IOrders orders)
	{
		this.orderId = orders.getOId();
	}

	@Override
	public IItem getItem()
	{
		return DatabaseHelper.getDatabase().findItemById( this.itemId );
	}

	@Override
	public void setItem(IItem item)
	{
		this.itemId = item.getIId();
	}

	@Override
	public Integer getOlQty()
	{
		return this.olQty;
	}

	@Override
	public void setOlQty(Integer olQty)
	{
		this.olQty = olQty;
	}

	@Override
	public Double getOlDiscount()
	{
		return this.olDiscount;
	}

	@Override
	public void setOlDiscount(Double oL_DISCOUNT)
	{
		this.olDiscount = oL_DISCOUNT;
	}

	@Override
	public String getOlComment()
	{
		return this.olComment;
	}

	@Override
	public void setOlComment(String olComment)
	{
		this.olComment = olComment;
	}
}
