package eu.cloudscale.showcase.db.dao.hibernate.impl;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IDao;

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
//		try
//        {
//	        System.out.println("SessionFactory = " + sFactory.getReference());
//        }
//        catch ( NamingException e )
//        {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//        }
		sessionFactory = sFactory;
	}
	
	protected final Session getCurrentSession() 
	{
		Session session = sessionFactory.getCurrentSession();
		//System.out.println("SESSION = " + session.getClass().hashCode());
        return sessionFactory.getCurrentSession();
    }
	
	@Override
//	@Transactional(readOnly=false)
	public T shrani(T object) 
	{	
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
