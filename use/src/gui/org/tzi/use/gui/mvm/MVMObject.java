package org.tzi.use.gui.mvm;

import java.util.List;

public class MVMObject implements Comparable<MVMObject>{

	private String name;
	private String className;
	private List<MVMAttribute> attributes;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<MVMAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<MVMAttribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public int compareTo(MVMObject otroObjeto) {
		// Comparar por el nombre (name) de los objetos
		return this.name.compareTo(otroObjeto.name);
	}
}
