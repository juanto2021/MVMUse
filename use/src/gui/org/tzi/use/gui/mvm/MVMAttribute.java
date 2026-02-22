package org.tzi.use.gui.mvm;
public class MVMAttribute {
	private String name;
	private String value;
	
    public MVMAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
	
	public MVMAttribute() {

	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
