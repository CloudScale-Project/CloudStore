package eu.cloudscale.showcase.db.dao.mongo.impl;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.dao.ICountryDao;
import eu.cloudscale.showcase.db.model.ICountry;
import eu.cloudscale.showcase.db.model.mongo.Country;

@Repository("mongoCountryDao")
public class CountryDaoImpl extends DaoImpl<ICountry> implements ICountryDao
{
	public CountryDaoImpl()
	{
		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );
	}

    @Override
    public ICountry findById(int id)
    {
	    return mongoOps.findOne( Query.query( Criteria.where( "coId" ).is(id) ), Country.class );
    }

	@Override
    public ICountry getByName(String country)
    {
		return mongoOps.findOne( Query.query( Criteria.where( "coName" ).regex(country, "i")), Country.class );
    }
	
	@Override
	public ICountry getObject()
	{
	    return new Country(); 
	}
	
	@Override
	public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "coId" );
	
	    Country o = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), Country.class);
	    
	    if( o == null || o.getCoId() == null)
	    	return 0;
	    
	    return o.getCoId();
    }
	
	@Override
	public ICountry shrani(ICountry object)
	{
		if( object.getCoId() == null)
		{
			object.setCoId( getLastId() +1 );
		}
		
		return super.shrani( object );
	}

	@Override
    public void createTable()
    {
    }
}
