package eu.cloudscale.showcase.db.model;

import java.util.Date;



public interface IAuthor
{

	public Integer getAId();

	public void setAId(Integer AId);

	public String getAFname();

	public void setAFname(String AFname);

	public String getALname();

	public void setALname(String ALname);

	public String getAMname();

	public void setAMname(String AMname);

	public Date getADob();

	public void setADob(Date ADob);

	public String getABio();

	public void setABio(String ABio);

}
