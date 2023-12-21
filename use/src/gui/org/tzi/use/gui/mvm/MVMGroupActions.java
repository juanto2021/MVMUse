package org.tzi.use.gui.mvm;
import java.util.Date;
import java.util.List;

public class MVMGroupActions {

	private String nameGroup;
	private Date creationDate;
	private Date lastModifiedDate;
	private String Description;
	private String modelName;
	private String srcFileUSE;
//	private List<MVMObject> lObjs;
//	private List<MVMLink> lLinks;
	private List<MVMAction> lActions;
	
	public String getnameGroup() {
		return nameGroup;
	}
	public void setnameGroup(String nameGroup) {
		this.nameGroup = nameGroup;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getSrcFileUSE() {
		return srcFileUSE;
	}
	public void setSrcFileUSE(String srcFileUSE) {
		this.srcFileUSE = srcFileUSE;
	}
//	public List<MVMObject> getlObjs() {
//		return lObjs;
//	}
//	public void setlObjs(List<MVMObject> lObjs) {
//		this.lObjs = lObjs;
//	}
//	public List<MVMLink> getlLinks() {
//		return lLinks;
//	}
//	public void setlLinks(List<MVMLink> lLinks) {
//		this.lLinks = lLinks;
//	}
	public List<MVMAction> getlActions() {
		return lActions;
	}
	public void setlActions(List<MVMAction> lActions) {
		this.lActions = lActions;
	}
	
}
