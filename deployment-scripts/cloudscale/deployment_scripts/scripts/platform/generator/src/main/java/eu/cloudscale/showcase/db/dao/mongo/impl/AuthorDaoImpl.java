package eu.cloudscale.showcase.db.dao.mongo.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.common.Soundex;
import eu.cloudscale.showcase.db.dao.IAuthorDao;
import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.mongo.Author;

@Repository("mongoAuthorDao")
public class AuthorDaoImpl extends DaoImpl<IAuthor> implements IAuthorDao
{
	public AuthorDaoImpl()
	{
		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );
	}
	
//	public AuthorDaoImpl(MongoTemplate mongoOps)
//    {
//	    super( mongoOps );
//    }
	
    @SuppressWarnings( {"unchecked", "rawtypes" } )
    @Override
	public List<IAuthor> findAll()
	{
		List authors = mongoOps.findAll( Author.class, "author" );
		return (List<IAuthor>) authors;
	}

    @Override
    public IAuthor findById(int id)
    {
	    return (IAuthor) mongoOps.findOne( Query.query( Criteria.where( "authorId" ).is( id ) ), Author.class);
    }
    
    @Override
    public IAuthor getObject()
    {
    	return new Author();
    }
    
    @Override
	public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "authorId" );
	
	    Author a = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), Author.class);
	    
	    if( a == null || a.getAId() == null)
	    	return 0;
	    
	    return a.getAId();
    }
	
	@Override
	public IAuthor shrani(IAuthor object)
	{
		if( object.getAId() == null)
		{
			object.setAId( getLastId() +1 );
		}
		
		return super.shrani( object );
	}

	@SuppressWarnings( {"rawtypes", "unchecked" } )
	@Override
    public List<IAuthor> findBySoundexLname(String keyword)
    {
	    List res = mongoOps.find( Query.query( Criteria.where( "lNameSoundex" ).is( Soundex.soundex( keyword ) ) ).limit( 50 ), Author.class );
	    
	    return res;
    }
}
