package eu.cloudscale.showcase.db.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IShoppingCartLineDao;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;
import eu.cloudscale.showcase.db.model.hibernate.ShoppingCartLine;

@Repository
@Transactional(readOnly=true)
public class ShoppingCartLineDaoImpl extends DaoImpl<IShoppingCartLine> implements IShoppingCartLineDao
{

	
	public ShoppingCartLineDaoImpl()
	{
//		super( (SessionFactory) ContextHelper.getApplicationContext().getBean( "sessionFactory" ) );
	}
	
	@Autowired
	public ShoppingCartLineDaoImpl(SessionFactory sessionFactory)
	{
		super( sessionFactory );
	}

    @SuppressWarnings( "unchecked" )
    @Override
    public IShoppingCartLine getBySCandItem(Integer shoppingId, int itemId)
    {
		String hql1 = "SELECT SCL FROM ShoppingCartLine as SCL WHERE SCL.shoppingCart.scId = :scId AND SCL.item.IId = :itemId";
	    Query q1 = getCurrentSession().createQuery( hql1 );
	    q1.setMaxResults( 1 );
	    q1.setParameter( "scId", shoppingId );
	    q1.setParameter( "itemId", itemId);
	    
	    List<Object> res = q1.list();
	    if( res.isEmpty() )
	    	return null;
	    
	    return (IShoppingCartLine) res.get( 0 );
	    
    }

	@Override
    public void delete(IShoppingCartLine obj)
    {
		Session session = getCurrentSession();
		session.delete( obj );
    }

	@SuppressWarnings( "unchecked" )
    @Override
    public boolean isCartEmpty(int scId)
    {
		String hql = "SELECT COUNT(SCL) FROM ShoppingCartLine as SCL WHERE SCL.shoppingCart.scId = :scId";
		Query q1 = getCurrentSession().createQuery( hql );
	    q1.setMaxResults( 1 );
	    q1.setParameter( "scId", scId);
	    
	    List<Long> res = q1.list();
	    if( res != null && res.get(0) == 0 )
	    	return true;
	    
	    return false;
    }

	@SuppressWarnings( "unchecked" )
    @Override
    public List<Object[]> findBySCId(Integer shoppingId)
    {
		String hql = "SELECT SCL, I FROM ShoppingCartLine as SCL, Item as I WHERE SCL.item.IId = I.IId AND SCL.shoppingCart.scId = :scId";
		Query q1 = getCurrentSession().createQuery( hql );
	    q1.setMaxResults( 1 );
	    q1.setParameter( "scId", shoppingId);
	    
	    List<Object[]> res = q1.list();
	    if( res.isEmpty() )
	    	return null;
	    
	    return res;
    }
    
    @Override
    public IShoppingCartLine getObject()
    {
        return new ShoppingCartLine();
    }
}
