package ch.origamiaddict.stripecontrol.config.mood;

import java.io.Serializable;

public class Mood implements IMood, Serializable{

	private static final long serialVersionUID = 8610473722894960819L;
	private String name;
	
	public Mood(String name, String value) {
		super();
		this.name = name;
		this.value = value;
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
	private String value;
}
