package eu.cloudscale.showcase.db.model;


public interface ICountry
{

	public Integer getCoId();

	public void setCoId(Integer coId);

	public String getCoName();

	public void setCoName(String coName);

	public Double getCoExchange();

	public void setCoExchange(Double coExchange);

	public String getCoCurrency();

	public void setCoCurrency(String coCurrency);

}
