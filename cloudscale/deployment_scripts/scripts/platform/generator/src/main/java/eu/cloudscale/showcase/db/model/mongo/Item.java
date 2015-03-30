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
import eu.cloudscale.showcase.db.model.IItem;

@Component
@Document( collection = "item" )
public class Item implements IItem, Serializable
{

	@Autowired
	@Qualifier("service")
	private IService service;
	
    private static final long serialVersionUID = -1311610290364285271L;

	@Id
	private ObjectId id;

	private Integer  iId;

	private Integer  authId;

	private String   iTitle;

	private Date     iPubDate;

	private String   iPublisher;

	private String   iSubject;

	private String   iDesc;

	private Integer  iRelated1;

	private Integer  iRelated2;

	private Integer  iRelated3;

	private Integer  iRelated4;

	private Integer  iRelated5;

	private String   iThumbnail;

	private String   iImage;

	private Double   iSrp;

	private Double   iCost;

	private Date     iAvail;

	private Integer  iStock;

	private String   iIsbn;

	private String   iPage;

	private String   iBacking;

	private String   iDimension;

	private Integer  olQty;

	/* for random selection */
	private double   iRandom;
	
	private String iTitleSoundex;

	public Integer getOlQty()
	{
		return olQty;
	}

	public void setOlQty(Integer olQty)
	{
		this.olQty = olQty;
	}

	public ObjectId getId()
	{
		return id;
	}

	public void setId(ObjectId id)
	{
		this.id = id;
	}

	
    public String getiTitleSoundex()
    {
    	return iTitleSoundex;
    }

	
    public void setiTitleSoundex(String iTitleSoundex)
    {
    	this.iTitleSoundex = iTitleSoundex;
    }

	@Override
	public Integer getIId()
	{
		return this.iId;
	}

	@Override
	public void setIId(Integer IId)
	{
		this.iId = IId;
	}

	@Override
	public IAuthor getAuthor()
	{
		return DatabaseHelper.getDatabase().getAuthorDaoImpl().findById( this.authId );
	}

	@Override
	public void setAuthor(IAuthor author)
	{
		this.setAuthId( author.getAId() );
	}

	@Override
	public String getITitle()
	{
		return this.iTitle;
	}

	@Override
	public void setITitle(String ITitle)
	{
		this.iTitle = ITitle;
	}

	@Override
	public Date getIPubDate()
	{
		return this.iPubDate;
	}

	@Override
	public void setIPubDate(Date IPubDate)
	{
		this.iPubDate = IPubDate;
	}

	@Override
	public String getIPublisher()
	{
		return this.iPublisher;
	}

	@Override
	public void setIPublisher(String IPublisher)
	{
		this.iPublisher = IPublisher;
	}

	@Override
	public String getISubject()
	{
		return this.iSubject;
	}

	@Override
	public void setISubject(String ISubject)
	{
		this.iSubject = ISubject;
	}

	@Override
	public String getIDesc()
	{
		return this.iDesc;
	}

	@Override
	public void setIDesc(String IDesc)
	{
		this.iDesc = IDesc;
	}

	@Override
	public Integer getIRelated1()
	{
		return this.iRelated1;
	}

	@Override
	public void setIRelated1(Integer IRelated1)
	{
		this.iRelated1 = IRelated1;
	}

	@Override
	public Integer getIRelated2()
	{
		return this.iRelated2;
	}

	@Override
	public void setIRelated2(Integer IRelated2)
	{
		this.iRelated2 = IRelated2;
	}

	@Override
	public Integer getIRelated3()
	{
		return this.iRelated3;
	}

	@Override
	public void setIRelated3(Integer IRelated3)
	{
		this.iRelated3 = IRelated3;
	}

	@Override
	public Integer getIRelated4()
	{
		return this.iRelated4;
	}

	@Override
	public void setIRelated4(Integer IRelated4)
	{
		this.iRelated4 = IRelated4;
	}

	@Override
	public Integer getIRelated5()
	{
		return this.iRelated5;
	}

	@Override
	public void setIRelated5(Integer IRelated5)
	{
		this.iRelated5 = IRelated5;
	}

	@Override
	public String getIThumbnail()
	{
		return this.iThumbnail;
	}

	@Override
	public void setIThumbnail(String IThumbnail)
	{
		this.iThumbnail = IThumbnail;
	}

	@Override
	public String getIImage()
	{
		return this.iImage;
	}

	@Override
	public void setIImage(String IImage)
	{
		this.iImage = IImage;
	}

	@Override
	public Double getISrp()
	{
		return this.iSrp;
	}

	@Override
	public void setISrp(Double i_SRP)
	{
		this.iSrp = i_SRP;
	}

	@Override
	public Double getICost()
	{
		return this.iCost;
	}

	@Override
	public void setICost(Double i_COST)
	{
		this.iCost = i_COST;
	}

	@Override
	public Date getIAvail()
	{
		return this.iAvail;
	}

	@Override
	public void setIAvail(Date IAvail)
	{
		this.iAvail = IAvail;
	}

	@Override
	public Integer getIStock()
	{
		return this.iStock;
	}

	@Override
	public void setIStock(Integer IStock)
	{
		this.iStock = IStock;
	}

	@Override
	public String getIIsbn()
	{
		return this.iIsbn;
	}

	@Override
	public void setIIsbn(String IIsbn)
	{
		this.iIsbn = IIsbn;
	}

	@Override
	public String getIPage()
	{
		return this.iPage;
	}

	@Override
	public void setIPage(String IPage)
	{
		this.iPage = IPage;
	}

	@Override
	public String getIBacking()
	{
		return this.iBacking;
	}

	@Override
	public void setIBacking(String IBacking)
	{
		this.iBacking = IBacking;
	}

	@Override
	public String getIDimension()
	{
		return this.iDimension;
	}

	@Override
	public void setIDimension(String IDimension)
	{
		this.iDimension = IDimension;
	}

	@Override
	public double getIRandom()
	{
		return iRandom;
	}

	@Override
	public void setIRandom(double iRandom)
	{
		this.iRandom = iRandom;
	}

	public Integer getAuthId()
	{
		return authId;
	}

	public void setAuthId(Integer authId)
	{
		this.authId = authId;
	}

}
