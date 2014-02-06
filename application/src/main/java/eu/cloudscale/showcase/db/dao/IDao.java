package eu.cloudscale.showcase.db.dao;


public interface IDao<T>
{
	public T shrani(T object);

	public void finish();
		
	public T getObject();
	
}
