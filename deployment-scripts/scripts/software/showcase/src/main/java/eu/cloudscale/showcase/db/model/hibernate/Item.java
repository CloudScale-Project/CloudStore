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
import org.hibernate.annotations.Index;

import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.IItem;
import eu.cloudscale.showcase.db.model.IOrderLine;
import eu.cloudscale.showcase.db.model.IShoppingCartLine;

/**
 * Item generated by hbm2java
 */
@Entity
@Table( name = "item", catalog = "tpcw" )
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

public class Item implements java.io.Serializable, IItem
{

	private static final long serialVersionUID  = 8806932749710043085L;

	private Integer           IId;

	private IAuthor           author;

	private String            ITitle;

	private Date              IPubDate;

	private String            IPublisher;

	private String            ISubject;

	private String            IDesc;

	private Integer           IRelated1;

	private Integer           IRelated2;

	private Integer           IRelated3;

	private Integer           IRelated4;

	private Integer           IRelated5;

	private String            IThumbnail;

	private String            IImage;

	private Double            ISrp;

	private Double            ICost;

	private Date              IAvail;

	private Integer           IStock;

	private String            IIsbn;

	private String            IPage;

	private String            IBacking;

	private String            IDimension;
	
	private double			  IRandom;

	private Set<IOrderLine>       orderLines        = new HashSet<IOrderLine>( 0 );

	private Set<IShoppingCartLine>       shoppingCartLines = new HashSet<IShoppingCartLine>( 0 );

	public Item()
	{
	}

	@Id
	@GeneratedValue( strategy = IDENTITY )
	@Column( name = "I_ID", unique = true, nullable = false )
	public Integer getIId()
	{
		return this.IId;
	}

	public void setIId(Integer IId)
	{
		this.IId = IId;
	}

	@ManyToOne( targetEntity=Author.class, fetch = FetchType.LAZY )
	@JoinColumn( name = "I_A_ID", nullable = false )
	public IAuthor getAuthor()
	{
		return this.author;
	}

	public void setAuthor(IAuthor author)
	{
		this.author = author;
	}

	@Column( name = "I_TITLE", length = 60 )
	public String getITitle()
	{
		return this.ITitle;
	}

	public void setITitle(String ITitle)
	{
		this.ITitle = ITitle;
	}

	@Temporal( TemporalType.TIMESTAMP )
	@Column( name = "I_PUB_DATE", length = 19 )
	public Date getIPubDate()
	{
		return this.IPubDate;
	}

	public void setIPubDate(Date IPubDate)
	{
		this.IPubDate = IPubDate;
	}

	@Column( name = "I_PUBLISHER", length = 60 )
	public String getIPublisher()
	{
		return this.IPublisher;
	}

	public void setIPublisher(String IPublisher)
	{
		this.IPublisher = IPublisher;
	}

	@Column( name = "I_SUBJECT", length = 60 )
	public String getISubject()
	{
		return this.ISubject;
	}

	public void setISubject(String ISubject)
	{
		this.ISubject = ISubject;
	}

	@Column( name = "I_DESC", length = 65535 )
	public String getIDesc()
	{
		return this.IDesc;
	}

	public void setIDesc(String IDesc)
	{
		this.IDesc = IDesc;
	}

	@Column( name = "I_RELATED1" )
	public Integer getIRelated1()
	{
		return this.IRelated1;
	}

	public void setIRelated1(Integer IRelated1)
	{
		this.IRelated1 = IRelated1;
	}

	@Column( name = "I_RELATED2" )
	public Integer getIRelated2()
	{
		return this.IRelated2;
	}

	public void setIRelated2(Integer IRelated2)
	{
		this.IRelated2 = IRelated2;
	}

	@Column( name = "I_RELATED3" )
	public Integer getIRelated3()
	{
		return this.IRelated3;
	}

	public void setIRelated3(Integer IRelated3)
	{
		this.IRelated3 = IRelated3;
	}

	@Column( name = "I_RELATED4" )
	public Integer getIRelated4()
	{
		return this.IRelated4;
	}

	public void setIRelated4(Integer IRelated4)
	{
		this.IRelated4 = IRelated4;
	}

	@Column( name = "I_RELATED5" )
	public Integer getIRelated5()
	{
		return this.IRelated5;
	}

	public void setIRelated5(Integer IRelated5)
	{
		this.IRelated5 = IRelated5;
	}

	@Column( name = "I_THUMBNAIL", length = 60 )
	public String getIThumbnail()
	{
		return this.IThumbnail;
	}

	public void setIThumbnail(String IThumbnail)
	{
		this.IThumbnail = IThumbnail;
	}

	@Column( name = "I_IMAGE", length = 60 )
	public String getIImage()
	{
		return this.IImage;
	}

	public void setIImage(String IImage)
	{
		this.IImage = IImage;
	}

	@Column( name = "I_SRP" )
	public Double getISrp()
	{
		return this.ISrp;
	}

	public void setISrp(Double i_SRP)
	{
		this.ISrp = i_SRP;
	}

	@Column( name = "I_COST" )
	public Double getICost()
	{
		return this.ICost;
	}

	public void setICost(Double i_COST)
	{
		this.ICost = i_COST;
	}

	@Temporal( TemporalType.TIMESTAMP )
	@Column( name = "I_AVAIL", length = 19 )
	public Date getIAvail()
	{
		return this.IAvail;
	}

	public void setIAvail(Date IAvail)
	{
		this.IAvail = IAvail;
	}

	@Column( name = "I_STOCK" )
	public Integer getIStock()
	{
		return this.IStock;
	}

	public void setIStock(Integer IStock)
	{
		this.IStock = IStock;
	}

	@Column( name = "I_ISBN", length = 13 )
	public String getIIsbn()
	{
		return this.IIsbn;
	}

	public void setIIsbn(String IIsbn)
	{
		this.IIsbn = IIsbn;
	}

	@Column( name = "I_PAGE", length = 4 )
	public String getIPage()
	{
		return this.IPage;
	}

	public void setIPage(String IPage)
	{
		this.IPage = IPage;
	}

	@Column( name = "I_BACKING", length = 15 )
	public String getIBacking()
	{
		return this.IBacking;
	}

	public void setIBacking(String IBacking)
	{
		this.IBacking = IBacking;
	}

	@Column( name = "I_DIMENSION", length = 25 )
	public String getIDimension()
	{
		return this.IDimension;
	}

	public void setIDimension(String IDimension)
	{
		this.IDimension = IDimension;
	}

	@OneToMany( targetEntity=OrderLine.class, fetch = FetchType.LAZY, mappedBy = "item" )
	public Set<IOrderLine> getOrderLines()
	{
		return this.orderLines;
	}

	public void setOrderLines(Set<IOrderLine> orderLines)
	{
		this.orderLines = orderLines;
	}

	@OneToMany( targetEntity=ShoppingCartLine.class, fetch = FetchType.LAZY, mappedBy = "item" )
	public Set<IShoppingCartLine> getShoppingCartLines()
	{
		return this.shoppingCartLines;
	}

	public void setShoppingCartLines(Set<IShoppingCartLine> shoppingCartLines)
	{
		this.shoppingCartLines = shoppingCartLines;
	}

	@Override
	@Column( name="I_RANDOM")
    public double getIRandom()
    {
	    return IRandom;
    }

	@Override
    public void setIRandom(double num)
    {
	    IRandom = num;	    
    }

}
