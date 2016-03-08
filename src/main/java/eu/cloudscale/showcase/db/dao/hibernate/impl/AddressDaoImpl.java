/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/
package eu.cloudscale.showcase.db.dao.hibernate.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IAddressDao;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.hibernate.Address;

@Repository
//@Transactional(readOnly=true)
public class AddressDaoImpl extends DaoImpl<IAddress> implements IAddressDao
{
	public AddressDaoImpl()
	{
		System.out.println("AddressDaoImpl constructor called!");
	}

	@Autowired
	public AddressDaoImpl(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

	@Override
    public List<IAddress> findAll()
    {
		return null;
    }

    @Override
    public IAddress findById(int id)
    {
		return (IAddress) getCurrentSession().get( Address.class, id );
    }
    
    @Override
    public IAddress getObject()
    {
    	return new Address();
    }
}
