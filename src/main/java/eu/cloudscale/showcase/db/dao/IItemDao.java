package eu.cloudscale.showcase.db.dao;

import java.util.List;

import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.IItem;


public interface IItemDao extends IDao<IItem>
{
	public List<IItem> findAll();
	public IItem findById(int id);
	public List<IItem> getPromotional();
	public List<IItem> getNewProducts(String category);
	public List<Object[]> getBestSellers(String category);
	public IItem getRandomItem();
	public IItem getObject();
	public List<IItem> findAllByAuthor(IAuthor author);
	public List<IItem> findAllByTitle(String keyword);
	public List<IItem> findAllBySubject(String keyword);
}
