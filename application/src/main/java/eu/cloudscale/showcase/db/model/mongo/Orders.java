package eu.cloudscale.showcase.db.model.mongo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import eu.cloudscale.showcase.db.IService;
import eu.cloudscale.showcase.db.common.DatabaseHelper;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.services.MongoService;

@Component
@Document( collection = "orders")
public class Orders implements IOrders, Serializable
{
	@Autowired
	@Qualifier("service")
	private IService service;
	
    private static final long serialVersionUID = -4867580403414098274L;

	@Id
	private ObjectId id;
	
	private Integer        oId;

	private Integer addressByOShipAddrId;
	
	private Integer       customer;

	private Integer addressByOBillAddrId;

	private Date           ODate;

	private Double        OSubTotal;

	private Double        OTax;

	private Double 		OTotal;

	private String         OShipType;

	private Date           OShipDate;

	private String         OStatus;
	
	public Orders()
	{

	}
	
    public ObjectId getId()
    {
    	return id;
    }

	
    public void setId(ObjectId id)
    {
    	this.id = id;
    }

	@Override
	public Integer getOId()
	{
		return this.oId;
	}
	
	@Override
	public void setOId(Integer OId)
	{
		this.oId = OId;
	}

	@Override
	public IAddress getAddressByOShipAddrId()
	{
		return DatabaseHelper.getDatabase().getAddressDaoImpl().findById( this.addressByOShipAddrId );
	}
	
	@Override
	public void setAddressByOShipAddrId(IAddress addressByOShipAddrId)
	{
		this.addressByOShipAddrId = addressByOShipAddrId.getAddrId();
	}

	@Override
	public ICustomer getCustomer()
	{
		return DatabaseHelper.getDatabase().getCustomerDaoImpl().findById( this.customer );
	}

	@Override
	public void setCustomer(ICustomer customer)
	{
		this.customer = customer.getCId();
	}

	@Override
	public IAddress getAddressByOBillAddrId()
	{
		return DatabaseHelper.getDatabase().getAddressDaoImpl().findById( this.addressByOBillAddrId );
	}

	@Override
	public void setAddressByOBillAddrId(IAddress addressByOBillAddrId)
	{
		this.addressByOBillAddrId = addressByOBillAddrId.getAddrId();
	}

	@Override
	public Date getODate()
	{
		return this.ODate;
	}

	@Override
	public void setODate(Date ODate)
	{
		this.ODate = ODate;
	}
	
	@Override
	public Double getOSubTotal()
	{
		return this.OSubTotal;
	}

	@Override
	public void setOSubTotal(Double o_SUB_TOTAL)
	{
		this.OSubTotal = o_SUB_TOTAL;
	}

	@Override
	public Double getOTax()
	{
		return this.OTax;
	}
	
	@Override
	public void setOTax(Double o_TAX)
	{
		this.OTax = o_TAX;
	}

	@Override
	public Double getOTotal()
	{
		return this.OTotal;
	}

	@Override
	public void setOTotal(double o_TOTAL)
	{
		this.OTotal = o_TOTAL;
	}

	@Override
	public String getOShipType()
	{
		return this.OShipType;
	}

	@Override
	public void setOShipType(String OShipType)
	{
		this.OShipType = OShipType;
	}

	@Override
	public Date getOShipDate()
	{
		return this.OShipDate;
	}

	@Override
	public void setOShipDate(Date OShipDate)
	{
		this.OShipDate = OShipDate;
	}
	
	@Override
	public String getOStatus()
	{
		return this.OStatus;
	}

	@Override
	public void setOStatus(String OStatus)
	{
		this.OStatus = OStatus;
	}

	@Override
    public Set<IOrderLine> getOrderLines()
    {
		List ordersList = DatabaseHelper.getDatabase().getOrderLineDaoImpl().findAllByOrder( this );
	    return new HashSet<IOrderLine>();
    }
}
