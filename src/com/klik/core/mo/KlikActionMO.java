package com.klik.core.mo;

import java.util.UUID;

import net.sf.json.JSONObject;

import com.netflix.astyanax.mapping.Column;
import com.netflix.astyanax.mapping.Id;

public class KlikActionMO {
	
	public static final String _NOT_ANALYZED="0";
	public static final String _ANALYZED="1";
	
	@Id("ID")
	private String id;
	@Column("TIME")
	private String time;
	@Column("LOCATION_URL")
	private String locationUrl;
	@Column("TARGET_URL")
	private String targetUrl;
	@Column("TARGET_TITLE")
	private String targetTitle;
	@Column("USER_ID")
	private String userId;
	@Column("USER_ID_TYPE")
	private String userIdType;
	@Column("ANALYZED")
	private String analyzed;
	@Column("IP")
	private String ip;
	@Column("BROWSER")
	private String browser;
	
	public KlikActionMO(){
		UUID uuid=UUID.randomUUID();
		this.id=uuid.toString();
		analyzed = _NOT_ANALYZED;
		time=System.currentTimeMillis()+"";
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLocationUrl() {
		return locationUrl;
	}

	public void setLocationUrl(String locationUrl) {
		this.locationUrl = locationUrl;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getTargetTitle() {
		return targetTitle.replaceAll(" ", "");
	}

	public void setTargetTitle(String targetTitle) {
		this.targetTitle = targetTitle;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserIdType() {
		return userIdType;
	}

	public void setUserIdType(String userIdType) {
		this.userIdType = userIdType;
	}

	public String getAnalyzed() {
		return analyzed;
	}

	public void setAnalyzed(String analyzed) {
		this.analyzed = analyzed;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public static KlikActionMO parseFromJson(String jsonString){
		KlikActionMO action=new KlikActionMO();
		JSONObject json=JSONObject.fromObject(jsonString);
		action.setId(json.get("id").toString());
		action.setTime(json.get("time").toString());
		action.setLocationUrl(json.get("locationUrl").toString());
		action.setTargetUrl(json.get("targetUrl").toString());
		action.setTargetTitle(json.get("targetTitle").toString());
		action.setUserId(json.get("userId").toString());
		action.setUserIdType(json.get("userIdType").toString());
		action.setAnalyzed(json.get("analyzed").toString());
		action.setIp(json.get("ip").toString());
		action.setBrowser(json.get("browser").toString());
		
		return action;
	}
	
}
