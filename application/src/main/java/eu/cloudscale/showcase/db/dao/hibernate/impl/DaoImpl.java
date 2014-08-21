package eu.cloudscale.showcase.db.dao.hibernate.impl;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IDao;

@Transactional
public class DaoImpl<T> implements IDao<T>
{
	protected int count = 1;
	protected SessionFactory sessionFactory;
	
	public DaoImpl()
    {
    }
	
	public DaoImpl(SessionFactory sessionFactory)
	{
		try
        {
	        System.out.println("SessionFactory = " + sessionFactory.getReference());
        }
        catch ( NamingException e )
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		this.sessionFactory = sessionFactory;
	}
	

	protected final Session getCurrentSession() 
	{
        return this.sessionFactory.getCurrentSession();
    }
	
	@Override
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
