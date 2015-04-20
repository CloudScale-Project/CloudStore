package eu.cloudscale.showcase.db.model;

import java.util.Set;



public interface IAddress
{

	public Integer getAddrId();

	public void setAddrId(Integer addrId);

	public  ICountry getCountry();

	public void setCountry(ICountry country);

	public String getAddrStreet1();

	public void setAddrStreet1(String addrStreet1);

	public String getAddrStreet2();

	public void setAddrStreet2(String addrStreet2);

	public String getAddrCity();

	public void setAddrCity(String addrCity);

	public String getAddrState();

	public void setAddrState(String addrState);

	public String getAddrZip();

	public void setAddrZip(String addrZip);

	public Set<ICustomer> getCustomers();

}
