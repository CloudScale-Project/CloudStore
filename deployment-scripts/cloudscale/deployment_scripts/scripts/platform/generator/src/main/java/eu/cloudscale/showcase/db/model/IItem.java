package eu.cloudscale.showcase.db.model;

import java.util.Date;


public interface IItem
{

	public Integer getIId();

	public void setIDimension(String IDimension);

	public String getIDimension();

	public void setIBacking(String IBacking);

	public String getIBacking();

	public void setIPage(String IPage);

	public String getIPage();

	public void setIIsbn(String IIsbn);

	public String getIIsbn();

	public void setIStock(Integer IStock);

	public Integer getIStock();

	public void setIAvail(Date IAvail);

	public Date getIAvail();

	public void setICost(Double i_COST);

	public Double getICost();

	public void setISrp(Double i_SRP);

	public Double getISrp();

	public void setIImage(String IImage);

	public String getIImage();

	public void setIThumbnail(String IThumbnail);

	public String getIThumbnail();

	public void setIRelated5(Integer IRelated5);

	public Integer getIRelated5();

	public void setIRelated4(Integer IRelated4);

	public Integer getIRelated4();

	public void setIRelated3(Integer IRelated3);

	public Integer getIRelated3();

	public void setIRelated2(Integer IRelated2);

	public Integer getIRelated2();

	public void setIRelated1(Integer IRelated1);

	public Integer getIRelated1();

	public void setIDesc(String IDesc);

	public String getIDesc();

	public void setISubject(String ISubject);

	public String getISubject();

	public void setIPublisher(String IPublisher);

	public String getIPublisher();

	public void setIPubDate(Date IPubDate);

	public Date getIPubDate();

	public void setITitle(String ITitle);

	public String getITitle();

	public void setAuthor(IAuthor author);

	public IAuthor getAuthor();

	public void setIId(Integer IId);
	
	public double getIRandom();
	
	public void setIRandom(double num);
}
