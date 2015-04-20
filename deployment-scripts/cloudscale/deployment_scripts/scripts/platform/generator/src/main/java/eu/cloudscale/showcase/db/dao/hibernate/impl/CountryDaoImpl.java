package eu.cloudscale.showcase.db.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.ICountryDao;
import eu.cloudscale.showcase.db.model.ICountry;
import eu.cloudscale.showcase.db.model.hibernate.Country;

@Repository
@Transactional(readOnly=true)
public class CountryDaoImpl extends DaoImpl<ICountry> implements ICountryDao
{
	private String tableName = "country";

	public CountryDaoImpl()
	{
//		super( (SessionFactory) ContextHelper.getApplicationContext().getBean( "sessionFactory" ) );
	}
	
	@Autowired
	public CountryDaoImpl(SessionFactory sessionFactory)
	{
		super( sessionFactory );
	}

    @Override
    public ICountry findById(int id)
    {
//		String hql = "FROM Country C WHERE C.coId = :coId";
//		Query q = this.session.createQuery(hql);
//		q.setParameter("coId", id);
//		List<Country> res = (List<Country>) q.list();
//		return res.get(0);
		return (ICountry) getCurrentSession().get( Country.class, id );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public ICountry getByName(String country)
    {
	    String hql = "SELECT C FROM Country as C WHERE C.coName = :countryName";
	    Query query = getCurrentSession().createQuery( hql );
	    query.setParameter( "countryName", country );
	    
	    List<Query> res = query.list();
	    if( res.isEmpty() )
	    {
	    	return null;
	    }
	    
	    return (ICountry) res.get( 0 );
    }

    @Override
	public ICountry getObject()
	{
		return new Country();
	}
    
    @Override
    public void createTable()
    {
    	String query = "CREATE TABLE IF NOT EXISTS " + this.tableName;
    	getCurrentSession().createSQLQuery( query );
    }
}
