package eu.cloudscale.showcase.db.model;

import java.util.Date;
import java.util.Set;


public interface IShoppingCart
{

	public void setScTime(Date scTime);

	public Date getScTime();

	public void setScId(Integer scId);

	public Integer getScId();

	public Set<IShoppingCartLine> getShoppingCartLines();

}
