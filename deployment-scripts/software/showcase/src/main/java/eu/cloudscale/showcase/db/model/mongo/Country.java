package eu.cloudscale.showcase.db.model.mongo;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import eu.cloudscale.showcase.db.model.ICountry;

@Document( collection = "country" )
public class Country implements ICountry, Serializable
{

	/**
     * 
     */
    private static final long serialVersionUID = 2938841459454938022L;

	@Id
	private ObjectId      id;

	private Integer        coId;

	private String        coName;

	private Double        coExchange;

	private String        coCurrency;

	public Country()
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
	public Integer getCoId()
	{
		return this.coId;
	}

	@Override
	public void setCoId(Integer coId)
	{
		this.coId = coId;
	}

	@Override
	public String getCoName()
	{
		return this.coName;
	}

	@Override
	public void setCoName(String coName)
	{
		this.coName = coName;
	}

	@Override
	public Double getCoExchange()
	{
		return this.coExchange;
	}

	@Override
	public void setCoExchange(Double coExchange)
	{
		this.coExchange = coExchange;
	}

	@Override
	public String getCoCurrency()
	{
		return this.coCurrency;
	}

	@Override
	public void setCoCurrency(String coCurrency)
	{
		this.coCurrency = coCurrency;
	}

}
