package eu.cloudscale.showcase.db.dao.mongo.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.dao.IOrderLineDao;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.model.mongo.OrderLine;

@Repository("mongoOrderLineDao")
public class OrderLineDaoImpl extends DaoImpl<IOrderLine> implements IOrderLineDao
{

	public OrderLineDaoImpl()
	{
		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );
	}
	
    @Override
	public IOrderLine getObject()
	{
		return new OrderLine();
	}

	@SuppressWarnings( {"unchecked", "rawtypes" } )
    @Override
    public List<IOrderLine> findAllByOrder(IOrders orders)
    {
	    List res = mongoOps.find( Query.query( Criteria.where( "orderId" ).is( orders.getOId() )), OrderLine.class );
	    return res;
    }
	
	@Override
	public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "olId" );
	
	    OrderLine o = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), OrderLine.class);
	    
	    if( o == null || o.getOlId() == null)
	    	return 0;
	    
	    return o.getOlId();
    }
	
	@Override
	public IOrderLine shrani(IOrderLine object)
	{
		if( object.getOlId() == null)
		{
			object.setOlId( getLastId() +1 );
		}
		
		return super.shrani( object );
	}
}
