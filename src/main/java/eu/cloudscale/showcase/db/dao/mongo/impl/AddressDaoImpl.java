/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/
package eu.cloudscale.showcase.db.dao.mongo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.dao.IAddressDao;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.mongo.Address;

@Repository("mongoAddressDao")
public class AddressDaoImpl extends DaoImpl<IAddress> implements IAddressDao
{
	
//	@Autowired
//	private MongoTemplate mongoOps;	
	
	public AddressDaoImpl()
	{
//		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );
		System.out.println("Created AddressDao instance");
	}
	
//	public AddressDaoImpl(MongoTemplate mongoOps)
//	{
//		super( mongoOps );
//	}

    @Override
	public List<IAddress> findAll()
	{
		return (List<IAddress>) mongoOps.findAll( IAddress.class, "address" );
	}

    @Override
    public IAddress findById(int id)
    {
	    return (IAddress) mongoOps.findOne( Query.query( Criteria.where( "addrId" ).is( id )), Address.class );
    }
	
    @Override
	public IAddress getObject()
	{
	    return new Address();
	}
    
    @Override
    public IAddress shrani(IAddress object)
    {
        if( object.getAddrId() == null)
        {
        	object.setAddrId( getLastId() + 1 );
        }
        return super.shrani( object );
    }

    @Override
	public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "addrId" );
	
	    IAddress a = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), Address.class);
	    
	    if( a == null || a.getAddrId() == null)
	    	return 0;
	    
	    return a.getAddrId(); 
    }
}
