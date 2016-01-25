/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.db.dao.mongo.impl;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.dao.IOrdersDao;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.model.mongo.Customer;
import eu.cloudscale.showcase.db.model.mongo.Orders;

@Repository("mongoOrdersDao")
public class OrdersDaoImpl extends DaoImpl<IOrders> implements IOrdersDao
{

	public OrdersDaoImpl()
	{
//		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );
	}

	@Override
    public IOrders getMostRecentOrder(ICustomer customer)
    {

		Sort sort = new Sort( Sort.Direction.DESC, "ODate" );
		Orders order = mongoOps.findOne( Query.query(Criteria.where( "customer" ).is(customer.getCId())).with( sort ), Orders.class );
		
		return order;
    }
	
    @Override
	public IOrders getObject()
	{
		return new Orders();
	}

	@Override
    public IOrders findById(Integer cxOId)
    {
		return mongoOps.findOne(Query.query(new Criteria().where( "oId" ).is( cxOId )), Orders.class);
    }
	
	@Override
	public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "oId" );
	
	    Orders o = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), Orders.class);
	    
	    if( o == null || o.getOId() == null)
	    	return 0;
	    
	    return o.getOId();
    }
	
	@Override
	public IOrders shrani(IOrders object)
	{
		if( object.getOId() == null)
		{
			object.setOId( getLastId() +1 );
		}
		
		return super.shrani( object );
	}
}
