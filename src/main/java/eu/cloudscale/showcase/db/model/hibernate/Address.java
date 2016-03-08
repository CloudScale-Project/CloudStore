/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*  
*  @author XLAB d.o.o.
*******************************************************************************/
package eu.cloudscale.showcase.db.model.hibernate;

// Generated May 16, 2013 3:07:18 PM by Hibernate Tools 4.0.0

import static javax.persistence.GenerationType.IDENTITY;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import eu.cloudscale.showcase.db.model.IAddress;
import eu.cloudscale.showcase.db.model.ICountry;
import eu.cloudscale.showcase.db.model.ICustomer;
import eu.cloudscale.showcase.db.model.IOrders;

/**
 * Address generated by hbm2java
 */
@Entity
@Table( name = "address", catalog = "tpcw" )
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Address implements IAddress
{

	private Integer        addrId;

	private ICountry        country;

	private String         addrStreet1;

	private String         addrStreet2;

	private String         addrCity;

	private String         addrState;

	private String         addrZip;

	private Set<IOrders>   ordersesForOBillAddrId = new HashSet<IOrders>( 0 );

	private Set<IOrders>   ordersesForOShipAddrId = new HashSet<IOrders>( 0 );

	private Set<ICustomer> customers              = new HashSet<ICustomer>( 0 );

	public Address()
	{
	}

	@Id
	@GeneratedValue( strategy = IDENTITY )
	@Column( name = "ADDR_ID", unique = true, nullable = false )
	@Override
	public Integer getAddrId()
	{
		return this.addrId;
	}

	public void setAddrId(Integer addrId)
	{
		this.addrId = addrId;
	}

	@ManyToOne( targetEntity=Country.class, fetch = FetchType.EAGER )
	@JoinColumn( name = "ADDR_CO_ID", nullable = false )
	@Override
	public ICountry getCountry()
	{
		return this.country;
	}

	@Override
	public void setCountry(ICountry country)
	{
		this.country = (Country) country;
	}

	@Column( name = "ADDR_STREET1", length = 40 )
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

	@Column( name = "ADDR_STREET2", length = 40 )
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

	@Column( name = "ADDR_CITY", length = 30 )
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

	@Column( name = "ADDR_STATE", length = 20 )
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

	@Column( name = "ADDR_ZIP", length = 10 )
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

	@OneToMany( targetEntity=Orders.class, fetch = FetchType.LAZY, mappedBy = "addressByOBillAddrId" )
	public Set<IOrders> getOrdersesForOBillAddrId()
	{
		return this.ordersesForOBillAddrId;
	}

	public void setOrdersesForOBillAddrId(Set<IOrders> ordersesForOBillAddrId)
	{
		this.ordersesForOBillAddrId = ordersesForOBillAddrId;
	}

	@OneToMany( targetEntity=Orders.class, fetch = FetchType.LAZY, mappedBy = "addressByOShipAddrId" )
	public Set<IOrders> getOrdersesForOShipAddrId()
	{
		return this.ordersesForOShipAddrId;
	}

	public void setOrdersesForOShipAddrId(Set<IOrders> ordersesForOShipAddrId)
	{
		this.ordersesForOShipAddrId = ordersesForOShipAddrId;
	}

	@OneToMany( targetEntity=Customer.class, fetch = FetchType.LAZY, mappedBy = "address" )
	public Set<ICustomer> getCustomers()
	{
		return this.customers;
	}

	public void setCustomers(Set<ICustomer> customers)
	{
		this.customers = customers;
	}

}
