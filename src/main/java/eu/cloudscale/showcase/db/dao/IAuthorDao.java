/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.db.dao;

import java.util.List;

import eu.cloudscale.showcase.db.model.IAuthor;

public interface IAuthorDao extends IDao<IAuthor>
{
	public List<IAuthor> findAll();

	public IAuthor findById(int id);

	public List<IAuthor> findBySoundexLname(String keyword);
	
}
