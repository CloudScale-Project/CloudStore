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
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.services.MongoService;

@Component
@Document( collection = "customer" )
public class Customer implements ICustomer, Serializable
{
	@Autowired
	@Qualifier("service")
	private IService service;
	
    private static final long serialVersionUID = 1920560375336316671L;

	@Id
	private ObjectId id;

	private Integer  cId;

	private Integer  addrId;

	private String   cUname;

	private String   cPasswd;

	private String   cFname;

	private String   cLname;

	private String   cPhone;

	private String   cEmail;

	private Date     cSince;

	private Date     cLastVisit;

	private Date     cLogin;

	private Date     cExpiration;

	private Double   cDiscount;

	private Double   cBalance;

	private Double   cYtdPmt;

	private Date     cBirthdate;

	private String   cData;
	
	public Customer()
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
	public Integer getCId()
	{
		return this.cId;
	}

	@Override
	public void setCId(Integer CId)
	{
		this.cId = CId;
	}

	@Override
	public IAddress getAddress()
	{
		return DatabaseHelper.getDatabase().getAddressDaoImpl().findById( addrId );
	}

	@Override
	public void setAddress(IAddress address)
	{
		this.addrId = address.getAddrId();
	}

	@Override
	public String getCUname()
	{
		return this.cUname;
	}

	@Override
	public void setCUname(String CUname)
	{
		this.cUname = CUname;
	}

	@Override
	public String getCPasswd()
	{
		return this.cPasswd;
	}

	@Override
	public void setCPasswd(String CPasswd)
	{
		this.cPasswd = CPasswd;
	}

	@Override
	public String getCFname()
	{
		return this.cFname;
	}

	@Override
	public void setCFname(String CFname)
	{
		this.cFname = CFname;
	}

	@Override
	public String getCLname()
	{
		return this.cLname;
	}

	@Override
	public void setCLname(String CLname)
	{
		this.cLname = CLname;
	}

	@Override
	public String getCPhone()
	{
		return this.cPhone;
	}

	@Override
	public void setCPhone(String CPhone)
	{
		this.cPhone = CPhone;
	}

	@Override
	public String getCEmail()
	{
		return this.cEmail;
	}

	@Override
	public void setCEmail(String CEmail)
	{
		this.cEmail = CEmail;
	}

	@Override
	public Date getCSince()
	{
		return this.cSince;
	}

	@Override
	public void setCSince(Date CSince)
	{
		this.cSince = CSince;
	}

	@Override
	public Date getCLastVisit()
	{
		return this.cLastVisit;
	}

	@Override
	public void setCLastVisit(Date CLastVisit)
	{
		this.cLastVisit = CLastVisit;
	}

	@Override
	public Date getCLogin()
	{
		return this.cLogin;
	}

	@Override
	public void setCLogin(Date CLogin)
	{
		this.cLogin = CLogin;
	}

	@Override
	public Date getCExpiration()
	{
		return this.cExpiration;
	}

	@Override
	public void setCExpiration(Date CExpiration)
	{
		this.cExpiration = CExpiration;
	}

	@Override
	public Double getCDiscount()
	{
		return this.cDiscount;
	}

	@Override
	public void setCDiscount(double c_DISCOUNT)
	{
		this.cDiscount = c_DISCOUNT;
	}

	@Override
	public Double getCBalance()
	{
		return this.cBalance;
	}

	@Override
	public void setCBalance(Double c_BALANCE)
	{
		this.cBalance = c_BALANCE;
	}

	@Override
	public Double getCYtdPmt()
	{
		return this.cYtdPmt;
	}

	@Override
	public void setCYtdPmt(Double c_YTD_PMT)
	{
		this.cYtdPmt = c_YTD_PMT;
	}

	@Override
	public Date getCBirthdate()
	{
		return this.cBirthdate;
	}

	@Override
	public void setCBirthdate(Date CBirthdate)
	{
		this.cBirthdate = CBirthdate;
	}

	@Override
	public String getCData()
	{
		return this.cData;
	}

	@Override
	public void setCData(String CData)
	{
		this.cData = CData;
	}
}
