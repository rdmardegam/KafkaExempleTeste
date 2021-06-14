package com.example.kafka.producer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Device {

	@JsonProperty("DeviceId")
	public String deviceId;
	@JsonProperty("DeviceName")
	public String deviceName;
	@JsonProperty("DeviceType")
	public Integer deviceType;
	@JsonProperty("SecureElementId")
	public String secureElementId;

}
