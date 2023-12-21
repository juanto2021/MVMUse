package org.tzi.use.gui.mvm;
import java.util.List;

public class MVMAction {

	private int orden;
	private String typeAction;
	private String parameters;
	private List<MVMObject> lObjs;
	private List<MVMLink> lLinks;
	
	public int getOrden() {
		return orden;
	}
	public void setOrden(int orden) {
		this.orden = orden;
	}
	public String getTypeAction() {
		return typeAction;
	}
	public void setTypeAction(String typeAction) {
		this.typeAction = typeAction;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public List<MVMObject> getlObjs() {
		return lObjs;
	}
	public void setlObjs(List<MVMObject> lObjs) {
		this.lObjs = lObjs;
	}
	public List<MVMLink> getlLinks() {
		return lLinks;
	}
	public void setlLinks(List<MVMLink> lLinks) {
		this.lLinks = lLinks;
	}	
	
	
}
