package eu.cloudscale.showcase.db.dao.mongo.impl;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.dao.IShoppingCartDao;
import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;
import eu.cloudscale.showcase.db.model.mongo.ShoppingCart;
import eu.cloudscale.showcase.db.model.mongo.ShoppingCartLine;

@Repository("mongoShoppingCartDao")
public class ShoppingCartDaoImpl extends DaoImpl<IShoppingCart> implements IShoppingCartDao
{
	public ShoppingCartDaoImpl()
	{
		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );
	}

	@Override
    public Integer createEmptyCart()
    {
		IShoppingCart sc = new ShoppingCart();
		sc.setScId( getLastId() + 1 );
		sc.setScTime( new Date() );
		shrani( sc );
		
		return sc.getScId();
    }

	@Override
	public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "scId" );
	
	    ShoppingCart sc = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), ShoppingCart.class);
	    
	    if( sc == null || sc.getScId() == null)
	    	return 0;
	    
	    return sc.getScId();
    }

	@Override
    public IShoppingCart findById(Integer shoppingId)
    {
	    return mongoOps.findOne( Query.query(Criteria.where("scId").is(shoppingId)), ShoppingCart.class );
    }
	
    @Override
	public IShoppingCart getObject()
	{
		return new ShoppingCart();
	}

	@SuppressWarnings( {"unchecked", "rawtypes" } )
    @Override
    public List<IShoppingCartLine> findAllBySC(IShoppingCart shoppingCart)
    {
		List res = mongoOps.find( Query.query(Criteria.where( "shoppingCart" ).is( shoppingCart.getScId() )), ShoppingCartLine.class );
		return res;
    }
	
	@Override
	public IShoppingCart shrani(IShoppingCart object)
	{
	    if( object.getScId() == null )
	    {
	    	object.setScId( getLastId()+1 );
	    }
	    return super.shrani( object );
	}
	
}
