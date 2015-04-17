package eu.cloudscale.showcase.db.model.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import eu.cloudscale.showcase.db.common.DatabaseHelper;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICcXacts;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.services.IService;
import eu.cloudscale.showcase.db.services.MongoService;

@Component
@Document( collection = "orders")
public class Orders implements IOrders, Serializable
{
//	@Autowired
//	@Qualifier("service")
//
//	private MongoService service;
	
    private static final long serialVersionUID = -4867580403414098274L;

	@Id
	private ObjectId id;
	
	@Indexed
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
	
	private List<Integer> ccXactIds;

	private List<Integer> orderLinesIds;
	
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
		return DatabaseHelper.getDatabase().findAddressById( this.addressByOShipAddrId );
	}
	
	@Override
	public void setAddressByOShipAddrId(IAddress addressByOShipAddrId)
	{
		this.addressByOShipAddrId = addressByOShipAddrId.getAddrId();
	}

	@Override
	public ICustomer getCustomer()
	{
		return DatabaseHelper.getDatabase().findCustomerById( this.customer );
	}

	@Override
	public void setCustomer(ICustomer customer)
	{
		this.customer = customer.getCId();
	}

	@Override
	public IAddress getAddressByOBillAddrId()
	{
		return DatabaseHelper.getDatabase().findAddressById( this.addressByOBillAddrId );
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
    public HashSet<IOrderLine> getOrderLines()
    {
		HashSet<IOrderLine> orderLinesSet = new HashSet<IOrderLine>();
		if ( this.orderLinesIds != null )
		{
    		for( Integer olId : this.orderLinesIds )
    		{
    			IOrderLine orderLine = DatabaseHelper.getDatabase().findOrderLineById(olId);
    			orderLinesSet.add(orderLine);
    		}	
    	}
		
		return orderLinesSet;
    }

	public void setCcXactses(HashSet<ICcXacts> ccXacts)
    {
	    ArrayList<Integer> ccXactIds = new ArrayList<Integer>();
	    for(ICcXacts ccXact : ccXacts)
	    {
	    	ccXactIds.add(ccXact.getId());
	    }
	    this.ccXactIds = ccXactIds;
    }

	public void setOrderLines(HashSet<IOrderLine> set)
    {
	    ArrayList<Integer> list = new ArrayList<Integer>();
	    for (IOrderLine ol : set)
	    {
	    	list.add(ol.getOlId());
	    }
	    this.orderLinesIds = list;
    }

	public HashSet<ICcXacts> getCcXactses()
    {
		HashSet<ICcXacts> set = new HashSet<ICcXacts>();
		for(Integer ccXactId : this.ccXactIds)
		{
			ICcXacts ccXact = DatabaseHelper.getDatabase().findCcXactsById( ccXactId );
			set.add(ccXact);
		}
		
		return set;
    }
	
	
}
