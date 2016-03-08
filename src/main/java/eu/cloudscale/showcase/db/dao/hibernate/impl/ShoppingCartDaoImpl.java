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

import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IShoppingCartDao;
import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;
import eu.cloudscale.showcase.db.model.hibernate.ShoppingCart;

@Repository
public class ShoppingCartDaoImpl extends DaoImpl<IShoppingCart> implements IShoppingCartDao
{


	@Autowired
	public ShoppingCartDaoImpl(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

    @SuppressWarnings( "rawtypes" )
    @Override
//    @Transactional(readOnly=true)
    public IShoppingCart findById(Integer shoppingId)
    {
    	Session session = getCurrentSession();
	    String hql = "SELECT SC FROM ShoppingCart as SC WHERE SC.scId = :scId";
	    
	    Query query = session.createQuery(hql);
	    query.setParameter( "scId", shoppingId );
	    
	   
    	List res = query.list();
	    
	    if( res.isEmpty() )
	    {
//	    	System.out.println("results are empty! " + query.getQueryString());
	    	return null;
	    }
	    
	    ShoppingCart sc = (ShoppingCart) res.get( 0 );
	    Hibernate.initialize( sc.getShoppingCartLines() );
	    return sc;
    }

	@SuppressWarnings( "unchecked" )
    @Override
//    @Transactional(readOnly=true)
    public List<IShoppingCartLine> findAllBySC(IShoppingCart shoppingCart)
    {
		String hql = "SELECT SCL FROM ShoppingCartLine as SCL WHERE SCL.shoppingCart = :shoppingCart";
		
		Query query = getCurrentSession().createQuery( hql );
		query.setParameter( "shoppingCart", shoppingCart );
		
		return query.list();
    }
	
	@Override
	public IShoppingCart getObject()
	{
		return new ShoppingCart();
	}
	
}
