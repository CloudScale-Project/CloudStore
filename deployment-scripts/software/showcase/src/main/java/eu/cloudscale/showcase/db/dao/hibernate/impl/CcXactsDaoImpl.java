package eu.cloudscale.showcase.db.dao.hibernate.impl;


import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.ICcXactsDao;
import eu.cloudscale.showcase.db.model.ICcXacts;
import eu.cloudscale.showcase.db.model.hibernate.CcXacts;

@Repository
@Transactional(readOnly=true)
public class CcXactsDaoImpl extends DaoImpl<ICcXacts> implements ICcXactsDao
{
	
	public CcXactsDaoImpl()
	{
//		super( (SessionFactory) ContextHelper.getApplicationContext().getBean( "sessionFactory" ) );
	}
	
	@Autowired
	public CcXactsDaoImpl(SessionFactory sessionFactory)
	{
		super( sessionFactory );
	}
	
    @Override
	public ICcXacts getObject()
	{
		return new CcXacts();
	}

	@SuppressWarnings( "rawtypes" )
    @Override
    public int getLastCcXactsId()
    {
		String hql = "SELECT CX_ID FROM cc_xacts ORDER BY CX_ID DESC";
		
		Query query = getCurrentSession().createQuery( hql );
		query.setMaxResults( 1 );
		List res = query.list();
		
		return ((ICcXacts) res.get( 0 )).getId();
    }

	@SuppressWarnings( "unchecked" )
    @Override
    public ICcXacts findById(Integer id)
    {
		String hql = "SELECT * FROM cc_xacts WHERE CX_ID = :id";
		Query query = getCurrentSession().createQuery( hql );
		query.setParameter( "id", id );		
		
		List<CcXacts> res = query.list();
		
		if( res.isEmpty() )
			return null;
		
		return res.get( 0 );
    }

}
