package eu.cloudscale.showcase.db.dao.mongo.impl;

import eu.cloudscale.showcase.db.dao.IDao;


public interface IDaoExt<T> extends IDao<T>
{
	public Integer getLastId();

}
