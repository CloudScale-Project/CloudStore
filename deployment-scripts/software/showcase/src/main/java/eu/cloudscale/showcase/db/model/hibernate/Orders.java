package eu.cloudscale.showcase.db.model.hibernate;

// Generated May 16, 2013 3:07:18 PM by Hibernate Tools 4.0.0

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICcXacts;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IOrders;

@Entity
@Table( name = "orders", catalog = "tpcw" )
public class Orders implements IOrders
{

	private Integer      id;

	private IAddress     addressByOShipAddrId;

	private ICustomer    customer;

	private IAddress     addressByOBillAddrId;

	private Date        ODate;

	private Double      OSubTotal;

	private Double      OTax;

	private Double      OTotal;

	private String      OShipType;

	private Date        OShipDate;

	private String      OStatus;

	private Set<ICcXacts> ccXactses  = new HashSet<ICcXacts>( 0 );

	private Set<IOrderLine> orderLines = new HashSet<IOrderLine>( 0 );

	public Orders()
	{

	}

	@Id
	@GeneratedValue( strategy = IDENTITY )
	@Column( name = "O_ID", unique = true, nullable = false )
	public Integer getOId()
	
	{
		return this.id;
	}

	public void setOId(Integer OId)
	{
		this.id = OId;
	}

	@ManyToOne( targetEntity=Address.class, fetch = FetchType.EAGER )
	@JoinColumn( name = "O_SHIP_ADDR_ID", nullable = false )
	public IAddress getAddressByOShipAddrId()
	{
		return this.addressByOShipAddrId;
	}

	public void setAddressByOShipAddrId(IAddress addressByOShipAddrId)
	{
		this.addressByOShipAddrId = addressByOShipAddrId;
	}

	@ManyToOne( targetEntity=Customer.class, fetch = FetchType.EAGER )
	@JoinColumn( name = "O_C_ID", nullable = false )
	public ICustomer getCustomer()
	{
		return this.customer;
	}

	public void setCustomer(ICustomer customer)
	{
		this.customer = customer;
	}

	@ManyToOne( targetEntity=Address.class, fetch = FetchType.EAGER )
	@JoinColumn( name = "O_BILL_ADDR_ID", nullable = false )
	public IAddress getAddressByOBillAddrId()
	{
		return this.addressByOBillAddrId;
	}

	public void setAddressByOBillAddrId(IAddress addressByOBillAddrId)
	{
		this.addressByOBillAddrId = addressByOBillAddrId;
	}

	@Temporal( TemporalType.DATE )
	@Column( name = "O_DATE", length = 19 )
	public Date getODate()
	{
		return this.ODate;
	}

	public void setODate(Date ODate)
	{
		this.ODate = ODate;
	}

	@Column( name = "O_SUB_TOTAL" )
	public Double getOSubTotal()
	{
		return this.OSubTotal;
	}

	public void setOSubTotal(Double o_SUB_TOTAL)
	{
		this.OSubTotal = o_SUB_TOTAL;
	}

	@Column( name = "O_TAX" )
	public Double getOTax()
	{
		return this.OTax;
	}

	public void setOTax(Double o_TAX)
	{
		this.OTax = o_TAX;
	}

	@Column( name = "O_TOTAL" )
	public Double getOTotal()
	{
		return this.OTotal;
	}

	public void setOTotal(double o_TOTAL)
	{
		this.OTotal = o_TOTAL;
	}

	@Column( name = "O_SHIP_TYPE", length = 10 )
	public String getOShipType()
	{
		return this.OShipType;
	}

	public void setOShipType(String OShipType)
	{
		this.OShipType = OShipType;
	}

	@Temporal( TemporalType.DATE )
	@Column( name = "O_SHIP_DATE", length = 19 )
	public Date getOShipDate()
	{
		return this.OShipDate;
	}

	public void setOShipDate(Date OShipDate)
	{
		this.OShipDate = OShipDate;
	}

	@Column( name = "O_STATUS", length = 15 )
	public String getOStatus()
	{
		return this.OStatus;
	}

	public void setOStatus(String OStatus)
	{
		this.OStatus = OStatus;
	}

	@OneToMany( targetEntity=CcXacts.class, fetch = FetchType.LAZY, mappedBy = "orders" )
	public Set<ICcXacts> getCcXactses()
	{
		return this.ccXactses;
	}

	public void setCcXactses(Set<ICcXacts> ccXactses)
	{
		this.ccXactses = ccXactses;
	}

	@OneToMany( targetEntity=OrderLine.class, fetch = FetchType.LAZY, mappedBy = "orders" )
	public Set<IOrderLine> getOrderLines()
	{
		return this.orderLines;
	}

	public void setOrderLines(Set<IOrderLine> orderLines)
	{
		this.orderLines = orderLines;
	}
}
