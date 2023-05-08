package com.example.kafka.producer.model.push;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "pushAccountId", "pushAccountReceipt", "issuerInitiatedDigitizationData" })
public class PushAccountReceipt {

	@JsonProperty("pushAccountId")
	private String pushAccountId;
	@JsonProperty("pushAccountReceipt")
	private String pushAccountReceipt;
	@JsonProperty("issuerInitiatedDigitizationData")
	private String issuerInitiatedDigitizationData;
	public String getPushAccountId() {
		return pushAccountId;
	}
	public void setPushAccountId(String pushAccountId) {
		this.pushAccountId = pushAccountId;
	}
	public String getPushAccountReceipt() {
		return pushAccountReceipt;
	}
	public void setPushAccountReceipt(String pushAccountReceipt) {
		this.pushAccountReceipt = pushAccountReceipt;
	}
	public String getIssuerInitiatedDigitizationData() {
		return issuerInitiatedDigitizationData;
	}
	public void setIssuerInitiatedDigitizationData(String issuerInitiatedDigitizationData) {
		this.issuerInitiatedDigitizationData = issuerInitiatedDigitizationData;
	}
}