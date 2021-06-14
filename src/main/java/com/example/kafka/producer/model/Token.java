package com.example.kafka.producer.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class Token{
	@JsonProperty("TokenUniqueReference") 
    public String tokenUniqueReference;
    @JsonProperty("PrimaryAccountNumberUniqueReference") 
    public String primaryAccountNumberUniqueReference;
    @JsonProperty("TokenSuffix") 
    public String tokenSuffix;
    @JsonProperty("ExpirationDate") 
    public String expirationDate;
    @JsonProperty("DigitizationRequestDateTime") 
    public OffsetDateTime digitizationRequestDateTime;
    @JsonProperty("TokenActivatedDateTime") 
    public OffsetDateTime tokenActivatedDateTime;
    @JsonProperty("FinalTokenizationDecision") 
    public String finalTokenizationDecision;
    @JsonProperty("CorrelationId") 
    public String correlationId;
    @JsonProperty("CurrentStatusCode") 
    public String currentStatusCode;
    @JsonProperty("CurrentStatusDescription") 
    public String currentStatusDescription;
    @JsonProperty("CurrentStatusDateTime") 
    public OffsetDateTime currentStatusDateTime;
    @JsonProperty("ProvisioningStatusCode") 
    public String provisioningStatusCode;
    @JsonProperty("ProvisioningStatusDescription") 
    public String provisioningStatusDescription;
    @JsonProperty("TokenRequestorId") 
    public String tokenRequestorId;
    @JsonProperty("WalletId") 
    public String walletId;
    @JsonProperty("PaymentAppInstanceId") 
    public String paymentAppInstanceId;
    @JsonProperty("TokenType") 
    public String tokenType;
    @JsonProperty("StorageTechnology") 
    public String storageTechnology;
    @JsonProperty("LastCommentId") 
    public String lastCommentId;
    @JsonProperty("TokenRequestorConsumerFacingEntityName") 
    public String tokenRequestorConsumerFacingEntityName;
    @JsonProperty("Device") 
    public Device device;
    /*@JsonProperty("Suspenders") 
    public Suspenders suspenders;*/
    
    @JsonProperty("AccountPanSequenceNumber") 
    public String accountPanSequenceNumber;
    
    @JsonProperty("ActivationCodeExpirationDateTime") 
    public OffsetDateTime activationCodeExpirationDateTime;
    
    @JsonProperty("TokenAssuranceLevel") 
    public int tokenAssuranceLevel;
    
    @JsonProperty("TokenRequestorName") 
    public String tokenRequestorName;
}
