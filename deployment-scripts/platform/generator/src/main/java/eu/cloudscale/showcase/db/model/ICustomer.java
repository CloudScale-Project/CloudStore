package eu.cloudscale.showcase.db.model;

import java.util.Date;


public interface ICustomer
{

	public Integer getCId();

	public void setCId(Integer CId);

	public IAddress getAddress();

	public void setAddress(IAddress address);

	public String getCUname();

	public void setCUname(String CUname);

	public String getCPasswd();

	public void setCPasswd(String CPasswd);

	public String getCFname();

	public void setCFname(String CFname);

	public String getCLname();

	public void setCLname(String CLname);

	public String getCPhone();

	public void setCData(String CData);

	public String getCData();

	public void setCBirthdate(Date CBirthdate);

	public Date getCBirthdate();

	public void setCYtdPmt(Double c_YTD_PMT);

	public Double getCYtdPmt();

	public void setCBalance(Double c_BALANCE);

	public Double getCBalance();

	public void setCDiscount(double c_DISCOUNT);

	public Double getCDiscount();

	public void setCExpiration(Date CExpiration);

	public Date getCExpiration();

	public void setCLogin(Date CLogin);

	public Date getCLogin();

	public void setCLastVisit(Date CLastVisit);

	public Date getCLastVisit();

	public void setCSince(Date CSince);

	public Date getCSince();

	public void setCEmail(String CEmail);

	public String getCEmail();

	public void setCPhone(String CPhone);
}
