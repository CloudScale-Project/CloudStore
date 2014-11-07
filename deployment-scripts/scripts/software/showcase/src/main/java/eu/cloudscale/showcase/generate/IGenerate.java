package eu.cloudscale.showcase.generate;

import org.springframework.context.ApplicationContext;

public interface IGenerate
{
//	public void setContext(ApplicationContext ctx);
	public void populateCountryTable();
	
	public void populateAuthorTable();
	
	public void populateAddressTable();
	
	public void populateCustomerTable();
	
	public void populateItemTable();
	
	public void populateOrdersAndCC_XACTSTable();
}
