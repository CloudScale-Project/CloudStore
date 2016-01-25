/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.db.dao.mongo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.Mongo;

public abstract class DaoImpl<T> implements IDaoExt<T>
{
	@Autowired
	protected MongoTemplate mongoOps;
	
	public DaoImpl()
    {
    }
	
//	public DaoImpl(MongoTemplate mongoOps)
//	{
//		this.mongoOps = mongoOps;
//	}
	
	@Override
	@Transactional(readOnly=false)
	public T shrani(T object) 
	{	
		mongoOps.save( object );
		return object;
	}
	
	@Override
	@Transactional(readOnly=false)
	public T shrani(T object, boolean flag) 
	{	
		mongoOps.save( object );
		return object;
	}

	@Override
    public void finish()
    {
    }

	@Override
    public T getObject()
    {
		try
        {
	        throw new UnsupportedOperationException( "Implement in DAO implementation!" );
        }
        catch ( UnsupportedOperationException e )
        {
	        e.printStackTrace();
        }
       
		return null;
    }
}
