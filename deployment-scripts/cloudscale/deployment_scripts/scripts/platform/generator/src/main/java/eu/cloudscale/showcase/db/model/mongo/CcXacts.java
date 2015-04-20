package eu.cloudscale.showcase.db.model.mongo;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import eu.cloudscale.showcase.db.IService;
import eu.cloudscale.showcase.db.common.DatabaseHelper;
import eu.cloudscale.showcase.db.model.IAuthor;
import eu.cloudscale.showcase.db.model.ICcXacts;
import eu.cloudscale.showcase.db.model.ICountry;
import eu.cloudscale.showcase.db.model.IOrders;
import eu.cloudscale.showcase.db.services.MongoService;

@Component
@Document( collection = "ccxacts" )
public class CcXacts implements ICcXacts, Serializable
{

//	@Autowired
//	@Qualifier("service")
//	private IService service;;
	
    private static final long serialVersionUID = -8752879558532267562L;

	@Id
	private ObjectId id;
	
	private Integer   ccXactsId;

	private Integer cxCoId;

	private Integer cxOId;

	private String   cxType;

	private Integer  cxNum;

	private String   cxName;

	private Date     cxExpiry;

	private Integer cxAuthId;

	private Double   cxXactAmt;

	private Date     cxXactDate;
	
	public CcXacts()
	{
	}
	
    public void setId(ObjectId id)
    {
    	this.id = id;
    }

	@Override
	public Integer getId()
	{
		return this.ccXactsId;
	}

	@Override
	public void setId(Integer id)
	{
		this.ccXactsId = id;
	}

	@Override
	public ICountry getCountry()
	{
		return DatabaseHelper.getDatabase().getCountryDaoImpl().findById( this.cxCoId );
	}

	@Override
	public void setCountry(ICountry country)
	{
		this.cxCoId = country.getCoId();
	}

	@Override
	public IOrders getOrders()
	{
		return DatabaseHelper.getDatabase().getOrdersDaoImpl().findById( this.cxOId );
	}

	@Override
	public String getCxType()
	{
		return this.cxType;
	}

	@Override
	public void setCxType(String cxType)
	{
		this.cxType = cxType;
	}

	@Override
	public Integer getCxNum()
	{
		return this.cxNum;
	}

	@Override
	public void setCxNum(Integer cxNum)
	{
		this.cxNum = cxNum;
	}

	@Override
	public String getCxName()
	{
		return this.cxName;
	}

	@Override
	public void setCxName(String cxName)
	{
		this.cxName = cxName;
	}

	@Override
	public Date getCxExpiry()
	{
		return this.cxExpiry;
	}

	@Override
	public void setCxExpiry(Date cxExpiry)
	{
		this.cxExpiry = cxExpiry;
	}

	@Override
	public IAuthor getCxAuthId()
	{
		return DatabaseHelper.getDatabase().getAuthorDaoImpl().findById( this.cxAuthId );
	}

	@Override
	public void setCxAuthId(IAuthor cxAuthId)
	{
		this.cxAuthId = cxAuthId.getAId();
	}

	@Override
	public Double getCxXactAmt()
	{
		return this.cxXactAmt;
	}

	@Override
	public void setCxXactAmt(Double o_TOTAL)
	{
		this.cxXactAmt = o_TOTAL;
	}

	@Override
	public Date getCxXactDate()
	{
		return this.cxXactDate;
	}

	@Override
	public void setCxXactDate(Date cxXactDate)
	{
		this.cxXactDate = cxXactDate;
	}

	@Override
	public void setOrders(IOrders orders)
	{
		this.cxOId = orders.getOId();
	}
}
