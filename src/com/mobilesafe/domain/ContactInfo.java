package com.mobilesafe.domain;

public class ContactInfo {
	private String name;
	private String number;

	public String getName() {
		return name;
	}

	public ContactInfo() {
		super();
	}

	public ContactInfo(String name, String number) {
		super();
		this.name = name;
		this.number = number;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
