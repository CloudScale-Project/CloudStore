package eu.cloudscale.showcase.db.dao.mongo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import eu.cloudscale.showcase.db.common.ContextHelper;
import eu.cloudscale.showcase.db.common.Soundex;
import eu.cloudscale.showcase.db.dao.IItemDao;
import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.mongo.Item;

@Repository("mongoItemDao")
public class ItemDaoImpl extends DaoImpl<IItem> implements IItemDao
{
	public ItemDaoImpl()
	{
		super( (MongoTemplate) ContextHelper.getApplicationContext().getBean( "mongoTemplate" ) );
	}

    @SuppressWarnings( {"rawtypes", "unchecked" } )
    @Override
	public List<IItem> findAll()
	{
		List items = mongoOps.find( Query.query(new Criteria()).limit( 50 ), Item.class, "item");
		return (List<IItem>) items;
	}

    @Override
	public IItem findById(int id)
	{
		Item item = mongoOps.findOne( Query.query( Criteria.where( "IId" ).is( id ) ), Item.class);
		return item;
	}
	
    @Override
	public IItem getRandomItem()
	{
		return (IItem) mongoOps.findOne( Query.query( Criteria.where( "IRandom" ).gt( new Random().nextDouble() )), Item.class, "item" );
	}

	@Override
	public List<IItem> getPromotional() 
	{

		IItem item = getRandomItem();
		ArrayList<IItem> promotional = new ArrayList<IItem>();
		
		promotional.add( findById( item.getIRelated1()));
		
		promotional.add(findById( item.getIRelated2()));
		
		promotional.add(findById( item.getIRelated3()));
		
		promotional.add(findById( item.getIRelated4()));
		
		promotional.add(findById( item.getIRelated5()));
		
		return promotional;
	}

	@SuppressWarnings( {"rawtypes", "unchecked" } )
    @Override
    public List<IItem> getNewProducts(String category)
    {
		Sort sort = new Sort(Sort.Direction.DESC, "IPubDate").and( new Sort(Sort.Direction.ASC, "ITitle") );
		List items = mongoOps.find( Query.query(Criteria.where( "ISubject" ).is(category) ).with( sort ).limit( 50 ), Item.class );
		return (List<IItem>) items;
    }
	
	class ValueObject
	{
		private String id;
		private float value;
		
        public String getId()
        {
        	return id;
        }
		
        public void setId(String id)
        {
        	this.id = id;
        }
		
        public float getValue()
        {
        	return value;
        }
		
        public void setValue(float value)
        {
        	this.value = value;
        }
		
	}

	@Override
    public List<Object[]> getBestSellers(String category)
    {
    	Sort sort = new Sort( Sort.Direction.DESC, "olQty" );
    	
    	List<Item> res = mongoOps.find( Query.query(Criteria.where( "ISubject" ).is( category )).limit( 50 ).with( sort ), Item.class);
    	
    	ArrayList<Object[]> items = new ArrayList<Object[]>();
    	for( Item item : res)
    	{
    		Object[] itemObj = new Object[5];
    		itemObj[0] = item.getIId();
    		itemObj[1] = item.getITitle();
    		itemObj[2] = item.getAuthor().getAFname();
    		itemObj[3] = item.getAuthor().getALname();
    		itemObj[4] = item.getOlQty();
    		
    		items.add( itemObj );
    	}
    	
    	return items;
    	
//    	BasicDBObject groupFields = new BasicDBObject("_id", "$itemId");
//    	groupFields.put( "qty", new BasicDBObject("$sum", "$olQty") );
//    	
//    	BasicDBObject group = new BasicDBObject("$group", groupFields);
//    	
//    	BasicDBObject limit = new BasicDBObject("$limit", 3333);
//    	
//    	BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("qty", -1));
//    	
//    	AggregationOutput output = mongoOps.getCollection( mongoOps.getCollectionName( OrderLine.class ) ).aggregate( group, sort, limit );
//    	
//    	IItemDao itemDao = DatabaseHelper.getDatabase().getItemDaoImpl();
//    	ArrayList<Object[]> items = new ArrayList<Object[]>();
//    	
//    	for( DBObject object : output.results() )
//    	{
//    		if( items.size() == 50 )
//    		{
//    			break;
//    		}
//    		
//    		IItem item = itemDao.findById( (Integer) object.get( "_id" ) );
//    		
//    		if(item.getISubject().equals( category ))
//    		{
//        		Object[] itemObj = new Object[5];
//        		itemObj[0] = item.getIId();
//        		itemObj[1] = item.getITitle();
//        		itemObj[2] = item.getAuthor().getAFname();
//        		itemObj[3] = item.getAuthor().getALname(); 
//        		itemObj[4] = object.get( "qty" );
//        		items.add( itemObj );
//    		}
//    		   		
//    		
//    	}
//    	
//    	return items;
//    	DELA FUL POCAS!!! - glej http://stackoverflow.com/questions/4430407/mongodb-mapreduce-is-much-slower-than-pure-java-processing
//    	String mapFunction = "function()" +
//    			"{" +
//    			"	emit(this.itemId, this.olQty)" +
//    			"}";
//    	String reduceFunction = "function(k, v)" +
//    			"{" +
//    			"	return Array.sum(v)" +
//    			"}";
//    	MapReduceResults<ValueObject> results = mongoOps.mapReduce( "orderLine", mapFunction, reduceFunction, ValueObject.class );
    }
    
    @Override
    public IItem getObject()
    {
    	return new Item();
    }
    
    @Override
	public Integer getLastId()
    {
		Sort sort = new Sort(Sort.Direction.DESC, "iId" );
	
	    Item o = mongoOps.findOne(Query.query( new Criteria() ).with( sort ).limit( 1 ), Item.class);
	    
	    if( o == null || o.getIId() == null)
	    	return 0;
	    
	    return o.getIId();
    }
	
	@Override
	public IItem shrani(IItem object)
	{
		if( object.getIId() == null)
		{
			object.setIId( getLastId() +1 );
		}
		
		return super.shrani( object );
	}

    @SuppressWarnings( {"rawtypes", "unchecked" } )
    @Override
    public List<IItem> findAllByAuthor(IAuthor author)
    {
    	List res = mongoOps.find( Query.query( Criteria.where( "authId" ).is( author.getAId() ) ).limit( 50 ), Item.class );
	    return res;
    }

	@SuppressWarnings( {"rawtypes", "unchecked" } )
    @Override
    public List<IItem> findAllByTitle(String keyword)
    {
		List res = mongoOps.find( Query.query( Criteria.where( "iTitleSoundex" ).is( Soundex.soundex( keyword ) ) ).limit( 50 ), Item.class );
		
		return res;
    }

	@SuppressWarnings( {"rawtypes", "unchecked" } )
    @Override
    public List<IItem> findAllBySubject(String keyword)
    {
		List res = mongoOps.find( Query.query( Criteria.where( "iSubject" ).is( keyword )).limit( 50 ), Item.class );
		
	    return res;
    }
}
