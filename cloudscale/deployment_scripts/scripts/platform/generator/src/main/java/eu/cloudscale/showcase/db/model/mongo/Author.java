package eu.cloudscale.showcase.db.model.mongo;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import eu.cloudscale.showcase.db.model.IAuthor;

@Document( collection = "author" )
public class Author implements IAuthor, Serializable
{

	/**
     * 
     */
    private static final long serialVersionUID = -658143403409650089L;

	@Id
	private ObjectId id;

	private Integer  authorId;

	private String   AFname;

	private String   ALname;

	private String   AMname;

	private Date     ADob;

	private String   ABio;

	private String   lNameSoundex;

	public Author()
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
	public Integer getAId()
	{
		return this.authorId;
	}

	@Override
	public void setAId(Integer AId)
	{
		this.authorId = AId;
	}

	@Override
	public String getAFname()
	{
		return this.AFname;
	}

	@Override
	public void setAFname(String AFname)
	{
		this.AFname = AFname;
	}

	@Override
	public String getALname()
	{
		return this.ALname;
	}

	@Override
	public void setALname(String ALname)
	{
		this.ALname = ALname;
	}

	@Override
	public String getAMname()
	{
		return this.AMname;
	}

	@Override
	public void setAMname(String AMname)
	{
		this.AMname = AMname;
	}

	@Override
	public Date getADob()
	{
		return this.ADob;
	}

	@Override
	public void setADob(Date ADob)
	{
		this.ADob = ADob;
	}

	@Override
	public String getABio()
	{
		return this.ABio;
	}

	@Override
	public void setABio(String ABio)
	{
		this.ABio = ABio;
	}

	public String getlNameSoundex()
	{
		return lNameSoundex;
	}

	public void setlNameSoundex(String lNameSoundex)
	{
		this.lNameSoundex = lNameSoundex;
	}

}
