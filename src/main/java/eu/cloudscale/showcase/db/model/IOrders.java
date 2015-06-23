package eu.cloudscale.showcase.db.model;

import java.util.Date;
import java.util.Set;

public interface IOrders
{

	public Integer getOId();

	public void setOId(Integer OId);

	IAddress getAddressByOShipAddrId();

	void setAddressByOShipAddrId(IAddress addressByOShipAddrId);

	ICustomer getCustomer();

	void setCustomer(ICustomer customer);

	IAddress getAddressByOBillAddrId();

	void setAddressByOBillAddrId(IAddress addressByOBillAddrId);


	void setOStatus(String OStatus);

	String getOStatus();

	void setOShipDate(Date OShipDate);

	Date getOShipDate();

	void setOShipType(String OShipType);

	String getOShipType();

	void setOTotal(double o_TOTAL);

	Double getOTotal();

	void setOTax(Double o_TAX);

	Double getOTax();

	void setOSubTotal(Double o_SUB_TOTAL);

	Double getOSubTotal();

	void setODate(Date ODate);

	Date getODate();

	public Set<IOrderLine> getOrderLines();

}
