package eu.cloudscale.showcase.db.model;

import java.util.Date;



public interface ICcXacts
{

	public Integer getId();

	public void setId(Integer id);

	public ICountry getCountry();

	public void setCountry(ICountry country);

	public IOrders getOrders();

	public String getCxType();

	public void setCxType(String cxType);

	public Integer getCxNum();

	public void setCxNum(Integer cxNum);

	public String getCxName();

	public void setCxName(String cxName);

	public Date getCxExpiry();

	public void setCxExpiry(Date cxExpiry);

	public IAuthor getCxAuthId();

	public void setCxAuthId(IAuthor cxAuthId);

	public Double getCxXactAmt();

	public void setCxXactAmt(Double o_TOTAL);

	public Date getCxXactDate();

	public void setCxXactDate(Date cxXactDate);

	public void setOrders(IOrders orders);

}
