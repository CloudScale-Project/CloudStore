package eu.cloudscale.showcase.db.dao.mongo.impl;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.dao.ICcXactsDao;
import eu.cloudscale.showcase.db.model.ICcXacts;
import eu.cloudscale.showcase.db.model.mongo.CcXacts;

@Repository("mongoCcXactsDao")
public class CcXactsDaoImpl extends DaoImpl<ICcXacts> implements ICcXactsDao
{
	public CcXactsDaoImpl()
	{
		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );
	}
	
//	public CcXactsDaoImpl(MongoTemplate mongoOps)
//	{
//		super( mongoOps );
//	}
	
    @Override
	public ICcXacts getObject()
	{
		return new CcXacts();
	}
    
    @Override
    public ICcXacts findById(Integer id)
    {
		return (ICcXacts) mongoOps.findOne( Query.query(Criteria.where( "ccXactsId" ).is( id )), CcXacts.class);
    }


	@Override
    public int getLastCcXactsId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "ccXactsId" );		 
				
	    ICcXacts cc = mongoOps.findOne( Query.query(new Criteria()).with( sort ), CcXacts.class);
	    
	    
	    if( cc == null || cc.getId() == null)
	    	return 0;
	    
	    return cc.getId();    
	}

	@Override
	public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "ccXactsId" );
	
	    CcXacts o = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), CcXacts.class);
	    
	    if( o == null || o.getId() == null)
	    	return 0;
	    
	    return o.getId();
    }
	
	@Override
	public ICcXacts shrani(ICcXacts object)
	{
		if( object.getId() == null)
		{
			object.setId( getLastId() +1 );
		}
		
		return super.shrani( object );
	}

}
