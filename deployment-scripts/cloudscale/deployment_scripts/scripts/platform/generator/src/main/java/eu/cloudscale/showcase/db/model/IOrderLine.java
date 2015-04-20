package eu.cloudscale.showcase.db.model;


public interface IOrderLine
{

	public Integer getOlId();

	public void setOlId(Integer olId);

	public IOrders getOrders();

	public void setOrders(IOrders orders);

	public IItem getItem();

	public void setItem(IItem item);

	public Integer getOlQty();

	public void setOlQty(Integer olQty);

	public Double getOlDiscount();

	public void setOlDiscount(Double oL_DISCOUNT);

	public String getOlComment();

	public void setOlComment(String olComment);

}
