package com.example.kafka.producer.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Account{
	@JsonProperty("AccountPanSuffix") 
    public int accountPanSuffix;
    @JsonProperty("FinancialAccountSuffix") 
    public int financialAccountSuffix;
    @JsonProperty("CountryCode") 
    public String countryCode;
    @JsonProperty("InterbankCardAssociationId") 
    public long interbankCardAssociationId;
    @JsonProperty("InstitutionName") 
    public String institutionName;
    @JsonProperty("ExpirationDate") 
    public int expirationDate;
    @JsonProperty("AlternateAccountIdentifierSuffix") 
    public int alternateAccountIdentifierSuffix;
    @JsonProperty("Tokens") 
    public Tokens tokens;
    
    @Data
    public class Tokens{
        @JsonProperty("Token") 
        public List<Token> token;
    }


//    @JsonProperty("Tokens")
//    @JsonDeserialize(using = ItemsJsonDeserializer.class)
//  public ArrayList<Token> tokens;
    
    

/*
	private static class ItemsJsonDeserializer extends JsonDeserializer<List<Token>> {

		@Override
	    public List<Token> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
	    	InnerTokens innerItems = jp.readValueAs(InnerTokens.class);

	        return innerItems.elements;
	    }
	   
	    private static class InnerTokens {
	        
	    	public List<Token> elements;
	    }
	}*/
	

    
    
//  @SuppressWarnings("unchecked")
//  @JsonProperty("Tokens")
//  private void unpackNested(Map<String,Object> tokensMap) throws IOException {
//      //System.out.println("TESTE");
//      //System.out.println(tokens.get("Token"));
//      
//      //ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      
//      //this.tokens = mapper.readValues(tokens.get("Token"), new TypeReference<List<Token>>(){});
//      //this.tokens = Arrays.asList(mapper.convertValue(tokens.get("Token"), Token[].class));
//      
//  	/*System.out.println("TOKEN:::"); 
//  	System.out.println(tokens.get("Token"));
//  	
//  	Object obj = tokens.get("Token");
//  			
//  	List<Token> list = new ArrayList<Token>();
//      if (obj.getClass().isArray()) {
//          list = Arrays.asList((Token[])obj);
//      } else if (obj instanceof Collection) {
//          list = new ArrayList<>((Collection<Token>)obj);
//      }
//  	
//  	
//      //this.tokens = (ArrayList<Token>) tokens.get("Token");
//      System.out.println("----++++");
//      System.out.println(this.tokens);*/
//  	
//  	//ParentNode parentNode = mapper.readValue(jsonInString, ParentNode.class);
//      
//      //tokens.get(tokens);
//	  
//		Object obj = tokensMap.get("Token");
//
//		/*this.tokens = new ArrayList<Token>();
//		if (obj.getClass().isArray()) {
//			this.tokens = Arrays.asList((Token[]) obj);
//		} else if (obj instanceof Collection) {
//			this.tokens = new ArrayList<>((Collection<Token>) obj);
//		}*/
//		
//		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		byte[] json = mapper.writeValueAsBytes(obj);
//		this.tokens = mapper.readValue(json, ArrayList.class);
//      
//  }
    
}
