package eu.cloudscale.showcase.db.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.ICustomerDao;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.hibernate.Customer;

@Repository
@Transactional(readOnly=true)
public class CustomerDaoImpl extends DaoImpl<ICustomer> implements ICustomerDao
{

	public CustomerDaoImpl()
	{
//		super( (SessionFactory) ContextHelper.getApplicationContext().getBean( "sessionFactory" ) );
//		System.out.println("Normal constructor called!");
	}
	
	@Autowired
	public CustomerDaoImpl(SessionFactory sessionFactory)
	{
		super( sessionFactory );
	}

    @SuppressWarnings( "rawtypes" )
    @Override
    public ICustomer getUserBy(String username, String password)
    {
	    String query = "SELECT C FROM Customer as C WHERE C.CUname = :username AND C.CPasswd = :passwd";
	    
	    Query q = getCurrentSession().createQuery( query );
	    q.setMaxResults( 1 );
	    q.setParameter( "username", username );
	    q.setParameter( "passwd", password );
	    List res = q.list();
	    
	    if( res.isEmpty() )
	    	return null;
	    
	    return (ICustomer) q.list().get( 0 );
    }


    @Override
    public ICustomer getObject()
    {
	    return new Customer();
    }

    @Override
    public ICustomer findById(Integer id)
    {
		return (ICustomer) getCurrentSession().get( Customer.class, id );
    }

	@SuppressWarnings( "unchecked" )
    @Override
    public List<ICustomer> findByAddress(IAddress address)
    {
		String query = "SELECT C FROM Customer as C WHERE C.address = :address";
		
		Query q = getCurrentSession().createQuery( query );
		q.setParameter( "address", address );
		
		return q.list();
    }
}
