package eu.cloudscale.showcase.db.dao.hibernate.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IDao;

@Transactional(readOnly=true)
public class DaoImpl<T> implements IDao<T>
{
	protected int count = 1;
	protected SessionFactory sessionFactory;
	
	public DaoImpl()
    {
    }
	
	public DaoImpl(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}
	

	protected final Session getCurrentSession() 
	{
        return this.sessionFactory.getCurrentSession();
    }
	
	@Override
	@Transactional(readOnly=false)
	public T shrani(T object) 
	{	
    	getCurrentSession().saveOrUpdate( object );
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
