package com.example.kafka.producer.model.push;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "pushAccountReceipts", "availablePushMethods", "signature", "tokenRequestorSignatureSupport", "responseId" })
public class PushTokenRequestor {

	@JsonProperty("pushAccountReceipts")
	private List<PushAccountReceipt> pushAccountReceipts;
	@JsonProperty("availablePushMethods")
	private List<AvailablePushMethod> availablePushMethods;
	@JsonProperty("signature")
	private String signature;
	@JsonProperty("tokenRequestorSignatureSupport")
	private Boolean tokenRequestorSignatureSupport;
	@JsonProperty("responseId")
	private String responseId;
	
	@JsonProperty
	private String urlToCall;

	public List<PushAccountReceipt> getPushAccountReceipts() {
		return pushAccountReceipts;
	}

	public void setPushAccountReceipts(List<PushAccountReceipt> pushAccountReceipts) {
		this.pushAccountReceipts = pushAccountReceipts;
	}

	public List<AvailablePushMethod> getAvailablePushMethods() {
		return availablePushMethods;
	}

	public void setAvailablePushMethods(List<AvailablePushMethod> availablePushMethods) {
		this.availablePushMethods = availablePushMethods;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Boolean getTokenRequestorSignatureSupport() {
		return tokenRequestorSignatureSupport;
	}

	public void setTokenRequestorSignatureSupport(Boolean tokenRequestorSignatureSupport) {
		this.tokenRequestorSignatureSupport = tokenRequestorSignatureSupport;
	}

	public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	public String getUrlToCall() {
		return urlToCall;
	}

	public void setUrlToCall(String urlToCall) {
		this.urlToCall = urlToCall;
	}
	
}