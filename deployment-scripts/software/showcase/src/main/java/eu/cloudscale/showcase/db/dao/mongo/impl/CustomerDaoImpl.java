package eu.cloudscale.showcase.db.dao.mongo.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.dao.ICustomerDao;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.mongo.Customer;

@Repository("mongoCustomerDao")
public class CustomerDaoImpl extends DaoImpl<ICustomer> implements ICustomerDao
{
	public CustomerDaoImpl()
	{
		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );	
	}
	
//	public CustomerDaoImpl(MongoTemplate mongoOps)
//    {
//	    super( mongoOps );
//    }

    @Override
    public ICustomer findById(Integer id)
    {
		return mongoOps.findOne( Query.query(Criteria.where( "CId" ).is( id )), Customer.class);
    }

	@Override
    public ICustomer getUserBy(String username, String password)
    {
		return mongoOps.findOne( Query.query( Criteria.where( "CUname" ).is( username.toLowerCase()).and( "CPasswd" ).is(password.toLowerCase())), Customer.class );
    }
	
    @Override
	public ICustomer getObject()
	{
		return new Customer();
	}

	@SuppressWarnings( {"unchecked", "rawtypes" } )
    @Override
    public List<ICustomer> findByAddress(IAddress address)
    {
		List res = mongoOps.find( Query.query(Criteria.where( "addrId" ).is( address.getAddrId() )), Customer.class );
		
		return res;
    }

	@Override
    public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "cId" );
		
	    Customer c = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), Customer.class);
	    
	    if( c == null || c.getCId() == null)
	    	return 0;
	    
	    return c.getCId();
    }
	
	@Override
	public ICustomer shrani(ICustomer object)
	{
		if( object.getCId() == null)
		{
			object.setCId( getLastId() +1 );
		}
		
		return super.shrani( object );
	}
}
