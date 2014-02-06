package eu.cloudscale.showcase.db.dao.mongo.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.dao.IAddressDao;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.mongo.Address;

@Repository("mongoAddressDao")
public class AddressDaoImpl extends DaoImpl<IAddress> implements IAddressDao
{
	
//	@Autowired
//	private MongoTemplate mongoOps;	
	
	public AddressDaoImpl()
	{
		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );

	}
	
//	public AddressDaoImpl(MongoTemplate mongoOps)
//	{
//		super( mongoOps );
//	}

    @Override
	public List<IAddress> findAll()
	{
		return (List<IAddress>) mongoOps.findAll( IAddress.class, "address" );
	}

    @Override
    public IAddress findById(int id)
    {
	    return (IAddress) mongoOps.findOne( Query.query( Criteria.where( "addrId" ).is( id )), Address.class );
    }
	
    @Override
	public IAddress getObject()
	{
	    return new Address();
	}
    
    @Override
    public IAddress shrani(IAddress object)
    {
        if( object.getAddrId() == null)
        {
        	object.setAddrId( getLastId() + 1 );
        }
        return super.shrani( object );
    }

    @Override
	public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "addrId" );
	
	    IAddress a = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), Address.class);
	    
	    if( a == null || a.getAddrId() == null)
	    	return 0;
	    
	    return a.getAddrId(); 
    }
}
