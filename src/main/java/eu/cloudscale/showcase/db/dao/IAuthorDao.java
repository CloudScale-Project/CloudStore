package eu.cloudscale.showcase.db.dao;

import java.util.List;

import eu.cloudscale.showcase.db.model.IAuthor;

public interface IAuthorDao extends IDao<IAuthor>
{
	public List<IAuthor> findAll();

	public IAuthor findById(int id);

	public List<IAuthor> findBySoundexLname(String keyword);
	
}
