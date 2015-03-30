package eu.cloudscale.showcase.db.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IOrdersDao;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.model.hibernate.Orders;

@Repository
@Transactional(readOnly=true)
public class OrdersDaoImpl extends DaoImpl<IOrders> implements IOrdersDao
{
	public OrdersDaoImpl()
	{
//		super( (SessionFactory) ContextHelper.getApplicationContext().getBean( "sessionFactory" ) );
	}
	
	@Autowired
	public OrdersDaoImpl(SessionFactory sessionFactory)
	{
		super( sessionFactory );
	}
	
    @Override
	public IOrders getObject()
	{
		return new Orders();
	}

	@SuppressWarnings( { "rawtypes" } )
    @Override
    public IOrders getMostRecentOrder(ICustomer customer)
    {

		String hql16 = "SELECT O FROM Orders as O, Customer as C WHERE O.customer.CId = C.CId AND C.CUname = :cid ORDER BY O.ODate, O.OId DESC";
		Query query16 = getCurrentSession().createQuery( hql16 );
		query16.setMaxResults( 1 );
		query16.setParameter( "cid", customer.getCId() );
		List res16 = query16.list();
		
		if( res16 == null || res16.isEmpty() )
		{
			return null;
		}
		
		IOrders order = (IOrders) res16.get( 0 );
//		Hibernate.initialize( order.getOrderLines() );
		
		return order;
////		SELECT O FROM Customer as C, Orders as O WHERE C.CId = O.OCId AND C.CUname = 'SE' ORDER BY O.ODate, O.OId DESC
//		String hql1 = "SELECT O FROM Customer as C, Orders as O"
//					+ " WHERE C.CId = O.customer.CId"
//					+ " AND C.CUname = :username"
//					+ " ORDER BY O.ODate, O.OId DESC";
//		Query query = getCurrentSession().createQuery( hql1 );
//		query.setParameter( "username", customerUsername );
//		query.setMaxResults( 1 );
//		if( query.list().isEmpty() )
//			return null;
//		
//		IOrders order = (IOrders) query.list().get( 0 );
//		
//		String hql2 = "SELECT O, C, CX, SHIP, SHIP_CO, BILL, BILL_CO FROM"
//					+ " Orders as O, " 
//				    + " Customer as C," 
//				    + " CcXacts as CX, "
//				    + " Address as SHIP, "
//				    + " Address as BILL, "
//				    + " Country as SHIP_CO, "
//				    + " Country as BILL_CO "
//				    + " WHERE O.OId = :orderId"
//				    + " AND CX.id.cxOId = O.OId"
//					+ " AND C.CId = O.customer.CId"
//					+ " AND O.addressByOBillAddrId.addrId = BILL.addrId"
//					+ " AND BILL.country.coId = BILL_CO.coId"
//					+ " AND O.addressByOShipAddrId.addrId = SHIP.addrId"
//					+ " AND SHIP.country.coId = SHIP_CO.coId"
//					+ " ORDER BY O.ODate, O.OId DESC";
//		query = getCurrentSession().createQuery( hql2 );
//		query.setMaxResults(1);
//		query.setParameter("orderId", order.getOId());
//		
//		
//		List res = query.list();
//		if( res.isEmpty() )
//			return null;
//		
//		order = new Orders((Object[]) query.list().get( 0 ));
//		
//	    return order;
    }

	@Override
    public IOrders findById(Integer cxOId)
    {
		return (IOrders) getCurrentSession().get( Orders.class, cxOId );
    }
	
//	  @Override
//      public Orders getMostRecentOrder(String customerUsername, List<OrderLine> orderLines)
//      {
//    		String hql1 = "SELECT o_id "
//					+ "FROM customer, orders "
//					+ "WHERE customer.c_id = orders.o_c_id "
//					+ "AND c_uname = ? "
//					+ "ORDER BY o_date, orders.o_id DESC "
//					+ "LIMIT 0,1";
//    		Query query = getCurrentSession().createQuery( hql1 );
//    		query.setParameter( "username", customerUsername );
//    		query.setMaxResults( 1 );
//    		if( query.list().isEmpty() )
//    			return null;
//    		
//    		Orders order = (Orders) query.list().get( 0 );
//    		
//    		String hql2 = "SELECT orders.*, customer.*, "
//					+ "  cc_xacts.cx_type, "
//					+ "  ship.addr_street1 AS ship_addr_street1, "
//					+ "  ship.addr_street2 AS ship_addr_street2, "
//					+ "  ship.addr_state AS ship_addr_state, "
//					+ "  ship.addr_zip AS ship_addr_zip, "
//					+ "  ship_co.co_name AS ship_co_name, "
//					+ "  bill.addr_street1 AS bill_addr_street1, "
//					+ "  bill.addr_street2 AS bill_addr_street2, "
//					+ "  bill.addr_state AS bill_addr_state, "
//					+ "  bill.addr_zip AS bill_addr_zip, "
//					+ "  bill_co.co_name AS bill_co_name "
//					+ "FROM customer, orders, cc_xacts,"
//					+ "  address AS ship, "
//					+ "  country AS ship_co, "
//					+ "  address AS bill,  "
//					+ "  country AS bill_co "
//					+ "WHERE orders.o_id = :orderId "
//					+ "  AND cx_o_id = orders.o_id "
//					+ "  AND customer.c_id = orders.o_c_id "
//					+ "  AND orders.o_bill_addr_id = bill.addr_id "
//					+ "  AND bill.addr_co_id = bill_co.co_id "
//					+ "  AND orders.o_ship_addr_id = ship.addr_id "
//					+ "  AND ship.addr_co_id = ship_co.co_id "
//					+ "  AND orders.o_c_id = customer.c_id";
//    		query = getCurrentSession().createQuery( hql2 );
//    		query.setMaxResults(1);
//    		query.setParameter("orderId", order.getOId());
//    		
//    		List res = query.list();
//    		if( res.isEmpty() )
//    			return null;
//    		
//    		order = new Orders((Object[]) query.list().get( 0 ));
//    		
//    		String hql3 = "SELECT * "
//					+ "FROM order_line, item "
//					+ "WHERE ol_o_id = :orderId " + "AND ol_i_id = i_id";
//    		
//    		query = getCurrentSession().createQuery( hql3 );
//    		query.setParameter( "orderId", order.getOId() );
//    		List<Object[]> res3 = query.list();
//    		
//    		for( Object[] obj : res3 )
//    		{
//    			orderLines.add( (OrderLine) obj[0] );
//    		}
//    		
//    	    return order;
//      }
}
