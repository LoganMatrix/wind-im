package com.windchat.im.storage.bean;

import com.windchat.common.utils.GsonUtils;

public class SimpleAuthBean {
	private String siteUserId;
	private String deviceId;

	public String getSiteUserId() {
		return siteUserId;
	}

	public void setSiteUserId(String siteUserId) {
		this.siteUserId = siteUserId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String toString() {
		return GsonUtils.toJson(this);
	}

}
