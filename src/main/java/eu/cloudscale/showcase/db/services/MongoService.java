/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/
package eu.cloudscale.showcase.db.services;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;

import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.model.IShoppingCart;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;
import eu.cloudscale.showcase.db.model.mongo.Item;
import eu.cloudscale.showcase.db.model.mongo.Orders;

@Service
//@Component
public class MongoService extends AService
{
	public MongoService()
	{	
		System.out.println("Creating MongoService");
//		addressDao = ContextHelper.getApplicationContext().getBean( AddressDaoImpl.class );
	}
	
	
	@Override
	protected IOrders saveOrder(IAddress address, ICustomer customer,
	        String shipping, IShoppingCart sc, double discount)
	{
		Calendar cal = Calendar.getInstance();
		Random rand = new Random();

		Orders order = (Orders) ordersDao.getObject();
		order.setCustomer( customer );
		order.setOTax( 8.25 );
		order.setODate( new Date() );
		order.setAddressByOBillAddrId( customer.getAddress() );
		order.setAddressByOShipAddrId( address );

		cal.add( Calendar.DATE, rand.nextInt( 7 ) );
		order.setOShipDate( cal.getTime() );
		order.setOShipType( shipping );
		order.setOStatus( "PENDING" );
		order.setOTotal( calculateTotal( sc ) );
		// TODO: order.setOSubTotal( calculateSubTotal(sc) );

		order.setOSubTotal( calculateTotal( sc ) );
		//ordersDao.shrani( order );
		
		Set<IShoppingCartLine> res = sc.getShoppingCartLines();
		
		if( res != null && !res.isEmpty() )
		{
    		for ( IShoppingCartLine scl :  res)
    		{
    			IOrderLine ol = orderLineDao.getObject();
    			ol.setItem( scl.getItem() );
    			ol.setOlComment( getRandomString( 20, 100 ) );
    			ol.setOlDiscount( discount );
    			ol.setOlQty( scl.getSclQty() );
    			ol.setOrders( order );
    			
    			orderLineDao.shrani( ol );
    			
    			HashSet<IOrderLine> set = new HashSet<IOrderLine>();
				set.add(ol);
				set.addAll(order.getOrderLines());
    			
    			order.setOrderLines( set );
    			//ordersDao.shrani(order);
    			
    			Item item = (Item) itemDao.findById( scl.getItem().getIId() );

    			Integer olQty = item.getOlQty();
    			item.setOlQty( (olQty == null ? 0 : olQty ) + 1 );
    			
    			itemDao.shrani( item );
    
    			// TODO: Update item IStock property
    		}
		}

		ordersDao.shrani( order );

		return order;
	}
	
}
