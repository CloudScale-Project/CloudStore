package eu.cloudscale.showcase.db.dao.hibernate.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudscale.showcase.db.dao.IAuthorDao;
import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.hibernate.Author;

@Repository
@Transactional(readOnly=true)
public class AuthorDaoImpl extends DaoImpl<IAuthor> implements IAuthorDao
{
	public AuthorDaoImpl()
	{
//		super( (SessionFactory) ContextHelper.getApplicationContext().getBean( "sessionFactory" ) );
	}

	@Autowired
	public AuthorDaoImpl(SessionFactory sessionFactory)
	{
		super( sessionFactory );
	}

	@SuppressWarnings( "unchecked" )
    @Override
    public List<IAuthor> findAll()
    {
		String hql = "FROM Author";
		Query q = getCurrentSession().createQuery( hql );
		return q.list();
    }

    @Override
    public IAuthor findById(int id)
    {
		return (IAuthor) getCurrentSession().get( Author.class, id );
    }

    @Override
	public IAuthor getObject()
	{
		return new Author();
	}

	@Override
    public List<IAuthor> findBySoundexLname(String keyword)
    {
	    String hql = "SELECT A FROM Author as A, Item as I WHERE substring(soundex(A.ALname),0,4)=substring(soundex(:keyword),0,4) AND I.author.AId=A.AId ORDER BY I.ITitle";
	    
	    Query query = getCurrentSession().createQuery( hql );
	    query.setParameter( "keyword", keyword );
	    query.setMaxResults( 50 );
	    
	    List res = (List<Author>) query.list();
	    
	    return res;
    }
}
