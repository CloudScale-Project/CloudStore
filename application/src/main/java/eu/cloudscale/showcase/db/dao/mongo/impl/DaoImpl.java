package eu.cloudscale.showcase.db.dao.mongo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public abstract class DaoImpl<T> implements IDaoExt<T>
{
	@Autowired
	protected MongoTemplate mongoOps;
	
	public DaoImpl()
    {
    }
	
	public DaoImpl(MongoTemplate mongoOps)
	{
		this.mongoOps = mongoOps;
	}
	
	@Override
	public T shrani(T object) 
	{	
		mongoOps.save( object );
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
