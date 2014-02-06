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
@Transactional(readOnly=true)
public class ShoppingCartDaoImpl extends DaoImpl<IShoppingCart> implements IShoppingCartDao
{

	public ShoppingCartDaoImpl()
	{
//		super( (SessionFactory) ContextHelper.getApplicationContext().getBean( "sessionFactory" ) );
	}
	
	@Autowired
	public ShoppingCartDaoImpl(SessionFactory sessionFactory)
	{
		super(sessionFactory);
	}

    @Override
    @Transactional(readOnly=false)
    public Integer createEmptyCart()
    {
		IShoppingCart sc = new ShoppingCart();
		sc.setScTime( new Date() );
		shrani( sc );
		
		return sc.getScId();
    }

    @SuppressWarnings( "rawtypes" )
    @Override
    public IShoppingCart findById(Integer shoppingId)
    {
    	Session session = getCurrentSession();
	    String hql = "SELECT SC FROM ShoppingCart as SC WHERE SC.scId = :scId";
	    
	    Query query = session.createQuery(hql);
	    query.setParameter( "scId", shoppingId );
	    
	    List res = query.list();
	    if( res.isEmpty() )
	    	return null;
	    
	    ShoppingCart sc = (ShoppingCart) res.get( 0 );
	    Hibernate.initialize( sc.getShoppingCartLines() );
	    return sc;
    }

	@SuppressWarnings( "unchecked" )
    @Override
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
