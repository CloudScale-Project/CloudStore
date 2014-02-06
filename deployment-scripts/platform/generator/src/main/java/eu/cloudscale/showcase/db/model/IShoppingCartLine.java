package eu.cloudscale.showcase.db.model;



public interface IShoppingCartLine
{

	public IShoppingCart getShoppingCart();

	public void setShoppingCart(IShoppingCart shoppingCart);

	public Integer getSclId();

	public void setSclId(Integer sclScId);

	public IItem getItem();

	public void setItem(IItem item);

	public Integer getSclQty();

	public void setSclQty(Integer sclQty);

}
