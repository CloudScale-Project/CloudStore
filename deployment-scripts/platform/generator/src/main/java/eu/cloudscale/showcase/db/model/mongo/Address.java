package eu.cloudscale.showcase.db.model.mongo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import eu.cloudscale.showcase.db.common.DatabaseHelper;
import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICountry;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.services.MongoService;

@Component
@Document( collection = "address" )
public class Address implements IAddress, Serializable
{
	@Autowired
	private MongoService service;
	
	/**
     * 
     */
    private static final long serialVersionUID = 926688150220849693L;

	@Id
	private ObjectId      id;

	private Integer       addrId;

	private Integer		  coId;

	private String        addrStreet1;

	private String        addrStreet2;

	private String        addrCity;

	private String        addrState;

	private String        addrZip;
	
	public Address()
	{
		
	}

	public void setId(ObjectId id)
	{
		this.id = id;
	}

	public ObjectId getId()
	{
		return this.id;
	}

	@Override
	public Integer getAddrId()
	{
		return addrId;
	}

	@Override
	public void setAddrId(Integer addrId)
	{
		this.addrId = addrId;
	}

	@Override
	public ICountry getCountry()
	{
		return DatabaseHelper.getDatabase().getCountryDaoImpl().findById( this.coId );
	}

	@Override
	public void setCountry(ICountry country)
	{
		this.coId = country.getCoId();
	}

	@Override
	public String getAddrStreet1()
	{
		return this.addrStreet1;
	}

	@Override
	public void setAddrStreet1(String addrStreet1)
	{
		this.addrStreet1 = addrStreet1;
	}

	@Override
	public String getAddrStreet2()
	{
		return this.addrStreet2;
	}

	@Override
	public void setAddrStreet2(String addrStreet2)
	{
		this.addrStreet2 = addrStreet2;
	}

	@Override
	public String getAddrCity()
	{
		return this.addrCity;
	}

	@Override
	public void setAddrCity(String addrCity)
	{
		this.addrCity = addrCity;
	}

	@Override
	public String getAddrState()
	{
		return this.addrState;
	}

	@Override
	public void setAddrState(String addrState)
	{
		this.addrState = addrState;
	}

	@Override
	public String getAddrZip()
	{
		return this.addrZip;
	}

	@Override
	public void setAddrZip(String addrZip)
	{
		this.addrZip = addrZip;
	}

	@Override
    public Set<ICustomer> getCustomers()
    {
		if( this.addrId == null )
			return new HashSet<ICustomer>(0);
		
		IAddress address = DatabaseHelper.getDatabase().getAddressDaoImpl().findById( this.addrId );
		return new HashSet<ICustomer>(service.getCustomerDaoImpl().findByAddress( address )); 
    }

}
