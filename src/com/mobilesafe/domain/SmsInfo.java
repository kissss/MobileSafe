package com.mobilesafe.domain;

public class SmsInfo {

	/**
	 * 短信 id
	 */
	private String id;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 短信内容
	 */
	private String body;
	/**
	 * 短信类型 1,表示接受 2,表示发送
	 */
	private String type;
	/**
	 * 短信时间
	 */
	private String date;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
