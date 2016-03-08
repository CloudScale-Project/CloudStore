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

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IOrderLineDao;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.model.hibernate.OrderLine;

@Repository
//@Transactional(readOnly=true)
public class OrderLineDaoImpl extends DaoImpl<IOrderLine> implements IOrderLineDao
{

	public OrderLineDaoImpl()
	{
//		super( (SessionFactory) ContextHelper.getApplicationContext().getBean( "sessionFactory" ) );
	}
	
	@Autowired
	public OrderLineDaoImpl(SessionFactory sessionFactory)
	{
		super( sessionFactory );
	}
	
    @Override
	public IOrderLine getObject()
	{
		return new OrderLine();
	}

	@SuppressWarnings( "unchecked" )
    @Override
    public List<IOrderLine> findAllByOrder(IOrders orders)
    {
	    String hql = "SELECT OL FROM OrderLine as OL WHERE OL.orders = :order";
	    
	    Query query = getCurrentSession().createQuery( hql );
	    query.setParameter( "order", orders );
	    
	    return query.list();
    }

	@Override
    public IOrderLine findById(Integer id)
    {
		String hql = "SELECT OL FROM OrderLine as OL WHERE OL.id = :id";
		Query query = getCurrentSession().createQuery( hql );
		query.setParameter("id", id);
		
		return (IOrderLine) query.uniqueResult();
    }

}
