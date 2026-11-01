package com.file.utils;

import java.util.HashMap;
import java.util.Map;

import com.file.dto.FileViewDTO;

public class ServiceResponse {

	private String status;
	private String statusMsg;
	private String errorCode;
	private Map<String, Object> data = new HashMap<>();
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusMsg() {
		return statusMsg;
	}
	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public void addDataObject(String string, Object object) {
		Map<String,Object> data = new HashMap<>();
		data.put(string, object);
		this.setData(data);
	}
	
}
