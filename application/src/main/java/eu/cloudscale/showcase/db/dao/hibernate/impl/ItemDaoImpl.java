package eu.cloudscale.showcase.db.dao.hibernate.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IItemDao;
import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.hibernate.Item;

@Repository
@Transactional(readOnly=true)
public class ItemDaoImpl extends DaoImpl<IItem> implements IItemDao
{

	public ItemDaoImpl()
	{
		// super( (SessionFactory)
		// ContextHelper.getApplicationContext().getBean( "sessionFactory" ) );
	}

	@Autowired
	public ItemDaoImpl(SessionFactory sessionFactory)
	{
		super( sessionFactory );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List<IItem> findAll()
	{
		String hql = "FROM Item";
		Query q = getCurrentSession().createQuery( hql );
		q.setMaxResults( 20 );
		return (List<IItem>) q.list();
	}

	@SuppressWarnings( "unused" )
	@Override
	public IItem findById(int id)
	{
		String hql = "SELECT I, A FROM Item I, Author A WHERE I.IId = :itemId AND A.AId = I.author.AId";
		Query q = getCurrentSession().createQuery( hql );
		q.setParameter( "itemId", id );
		@SuppressWarnings( "rawtypes" )
		List res = q.list();
		if ( q.list().get( 0 ) == null )
			return null;

		IItem item = (Item) ( (Object[]) q.list().get( 0 ) )[0];
		// Hibernate.initialize( item.getShoppingCartLines() );
		// Hibernate.initialize( item.getOrderLines());
		return item;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List<IItem> getPromotional()
	{
		String hql = "FROM Item I ORDER BY RAND()";
		Query q = getCurrentSession().createQuery( hql );
		q.setMaxResults( 1 );

		List<IItem> res = (List<IItem>) q.list();

		ArrayList<IItem> promotional = new ArrayList<IItem>();
//
//		promotional.add( findById( 7296 ) );
//
//		promotional.add( findById( 6547 ) );
//
//		promotional.add( findById( 446 ) );
//
//		promotional.add( findById( 4807 ) );
//
//		promotional.add( findById( 5706 ) );
		 promotional.add( findById( res.get( 0 ).getIRelated1() ) );
		
		 promotional.add( findById( res.get( 0 ).getIRelated2() ) );
		
		 promotional.add( findById( res.get( 0 ).getIRelated3() ) );
		
		 promotional.add( findById( res.get( 0 ).getIRelated4() ) );
		
		 promotional.add( findById( res.get( 0 ).getIRelated5() ) );

		return promotional;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	@Transactional( readOnly = true )
	public List<IItem> getNewProducts(String category)
	{
		Session session = sessionFactory.getCurrentSession();
		Query query = session
		        .createQuery( "SELECT I, A FROM Item as I, Author as A WHERE I.author.AId = A.AId AND I.ISubject = :category "
		                + "ORDER BY I.IPubDate DESC, I.ITitle" );
		query.setString( "category", category );
		query.setMaxResults( 50 );
		ArrayList<IItem> newProducts = new ArrayList<IItem>();

		List<Object[]> res = query.list();
		for ( int i = 0; i < res.size(); i++ )
		{
			newProducts.add( (Item) ( res.get( i )[0] ) );
		}
		return newProducts;
	}

	@SuppressWarnings( "unchecked" )
	@Transactional( readOnly = true )
	public List<Object[]> getBestSellers(String category)
	{
		Session session = sessionFactory.openSession();

		 Query query =
		 session.createQuery("SELECT I.IId, A.AFname, A.ALname, I.ITitle, SUM(OL.olQty) AS val " +
		 		"FROM OrderLine as OL, Item as I, Author as A " +
		 		"WHERE " +
		 		"I.author.AId = A.AId AND " +
		 		"I.IId = OL.item.IId AND " +
		 		"I.ISubject = :category " +
		 		"GROUP BY OL.item.IId " +
		 		"ORDER BY val DESC");

//		Query query = session
//		        .createSQLQuery(
//		                "SELECT i_id as a, i_title as b, a_fname as c, a_lname as d, SUM(ol_qty) AS val "
//		                        + "FROM "
//		                        + "orders, order_line, item, author "
//		                        + "WHERE "
//		                        + "order_line.ol_o_id = orders.o_id AND item.i_id = order_line.ol_i_id "
//		                        + "AND item.i_subject = :category AND item.i_a_id = author.a_id GROUP BY i_id "
//		                        + "ORDER BY orders.o_date, val DESC" )
//		        .addScalar( "a" ).addScalar( "b" ).addScalar( "c" )
//		        .addScalar( "d" ).addScalar( "val" );

		query.setParameter( "category", category );
		query.setMaxResults( 50 );

		long start = System.nanoTime();
		List<Object[]> res = query.list();
		System.out.println( "[best-sellers] Category = " + category + ", cas izvajanja = "
		        + ( System.nanoTime() - start ) / 1E9 );

		return res;

	}

	@SuppressWarnings( "unchecked" )
	@Override
	public IItem getRandomItem()
	{
		String hql = "SELECT I FROM Item as I ORDER BY RAND()";
		Query query = getCurrentSession().createQuery( hql );
		query.setMaxResults( 1 );

		List<Object> res = query.list();
		if ( res.isEmpty() )
		{
			return null;
		}

		return (IItem) res.get( 0 );
	}

	@Override
	public IItem getObject()
	{
		return new Item();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List<IItem> findAllByAuthor(IAuthor author)
	{
		String hql = "SELECT I FROM Item as I WHERE I.author = :author ORDER BY I.ITitle";
		Query query = getCurrentSession().createQuery( hql );

		query.setParameter( "author", author );
		query.setMaxResults( 50 );
		// query.setCacheable( true );

		List<IItem> res = query.list();

		ArrayList<IItem> items = new ArrayList<IItem>();
		for ( IItem item : res )
		{
			Hibernate.initialize( item.getAuthor() );
			items.add( item );
		}

		return items;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List<IItem> findAllByTitle(String keyword)
	{
		String hql = "SELECT I FROM Item as I, Author as A WHERE I.author.AId = A.AId AND substring(soundex(I.ITitle), 0, 4) = substring(soundex(:title), 0, 4) ORDER BY I.ITitle";

		Query query = getCurrentSession().createQuery( hql );

		query.setParameter( "title", keyword );
		query.setMaxResults( 50 );
		// query.setCacheable( true );

		List<IItem> res = query.list();

		for ( IItem item : res )
		{
			Hibernate.initialize( item.getAuthor() );
		}

		return res;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List<IItem> findAllBySubject(String keyword)
	{
		String hql = "SELECT I FROM Item as I WHERE I.ISubject = :subject ORDER BY I.ITitle";

		Query query = getCurrentSession().createQuery( hql );

		query.setParameter( "subject", keyword );
		query.setMaxResults( 50 );
		// query.setCacheable( true );

		List<IItem> res = query.list();
		for ( IItem item : res )
		{
			Hibernate.initialize( item.getAuthor() );
		}

		return res;
	}
}
