/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/
package eu.cloudscale.showcase.db.dao.mongo.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.dao.IShoppingCartLineDao;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;
import eu.cloudscale.showcase.db.model.mongo.ShoppingCartLine;

@Repository("mongoShoppingCartLineDao")
public class ShoppingCartLineDaoImpl extends DaoImpl<IShoppingCartLine> implements IShoppingCartLineDao
{
	public ShoppingCartLineDaoImpl()
	{
//		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );	
	}
	
//	public ShoppingCartLineDaoImpl(MongoTemplate mongoOps)
//    {
//		super(mongoOps);
//    }

	@Override
    public IShoppingCartLine getBySCandItem(Integer shoppingId, int itemId)
    {
		IShoppingCartLine scl = mongoOps.findOne( Query.query( Criteria.where( "shoppingCart" ).is( shoppingId ).andOperator( Criteria.where( "item" ).is( itemId ) )), ShoppingCartLine.class );
			
		return scl;
    }

	@Override
    public void delete(IShoppingCartLine bySCandItem)
    {
	    
    }

	@Override
    public boolean isCartEmpty(int scId)
    {
		return mongoOps.count(Query.query(Criteria.where("shoppingCart").is(scId)), ShoppingCartLine.class) > 0;
    }

	@Override
    public List<Object[]> findBySCId(Integer shoppingId)
    {
	    return null;
    }
    
    @Override
    public IShoppingCartLine getObject()
    {
    	return new ShoppingCartLine();
    }

    @Override
	public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "sclId" );
	
	    ShoppingCartLine scl = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), ShoppingCartLine.class);
	    
	    if( scl == null || scl.getSclId() == null)
	    	return 0;
	    
	    return scl.getSclId();
    }
    
    @Override
    public IShoppingCartLine shrani(IShoppingCartLine object)
    {
    	if( object.getSclId() == null )
    	{
    		object.setSclId( getLastId() + 1 );
    	}
        return super.shrani( object );
    }

}
