/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.db.dao.hibernate.impl;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IDao;
import eu.cloudscale.showcase.db.model.IShoppingCart;

//@Transactional(readOnly=true)
public class DaoImpl<T> implements IDao<T>
{
	protected int count = 1;
	
	protected SessionFactory sessionFactory;
	
	public DaoImpl()
    {
    }
	

	public DaoImpl(SessionFactory sFactory)
	{
		sessionFactory = sFactory;
	}
	
	protected final Session getCurrentSession() 
	{
		Session session = sessionFactory.getCurrentSession();
		//System.out.println("SESSION = " + session.getClass().hashCode());
        return sessionFactory.getCurrentSession();
    }
	
	@Override
	public T shrani(T object) 
	{	
		//Session session = sessionFactory.getCurrentSession();
	    //session.saveOrUpdate(object);
		sessionFactory.getCurrentSession().saveOrUpdate( object );
    	return object;
	}
	
	@Override
	public T shrani(T object, boolean flag) 
	{	
		//Session session = sessionFactory.getCurrentSession();
	    //session.saveOrUpdate(object);
		//System.out.println("[DaoImpl.shrani(object, flag)] " + ((IShoppingCart) object).getScId());
		sessionFactory.getCurrentSession().saveOrUpdate( object );
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
