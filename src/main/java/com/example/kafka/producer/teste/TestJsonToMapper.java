package com.example.kafka.producer.teste;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import com.example.kafka.producer.utils.Splunk;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TestJsonToMapper {

	private static ObjectMapper mapper2;
	static {
		mapper2 = new ObjectMapper();
		mapper2.registerModule(new JavaTimeModule());
		mapper2.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper2.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper2.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper2.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
	
	
	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
		String x= "{\r\n"
				+ "  \"uri\" : \"https://sandbox.api.mastercard.com/mdes/csapi/v2/search\",\r\n"
				+ "  \"method\" : \"POST\",\r\n"
				+ "  \"body\" : {\r\n"
				+ " \"SearchRequest\": {\r\n"
				+ "   \"AccountPan\": \"987654321\",\r\n"
				+ "   \"ExcludeDeletedIndicator\": \"false\",\r\n"
				+ "   \"AuditInfo\": {\r\n"
				+ "      \"UserId\": \"A1435477\",\r\n"
				+ "      \"UserName\": \"John Smith\",\r\n"
				+ "      \"Organization\": \"Any Bank\",\r\n"
				+ "      \"Phone\": \"5555551234\"\r\n"
				+ "   }\r\n"
				+ " }\r\n"
				+ "}\r\n"
				+ "}";
		
		String y = "{\r\n"
				+ "  \"uri\" : \"https://sandbox.api.mastercard.com/mdes/csapi/v2/search\",\r\n"
				+ "  \"method\" : \"POST\",\r\n"
				+ "  \"body\" : {\r\n"
				+ " \"SearchRequest\": {\r\n"
				+ "   \"AccountPan\": \"987654321\",\r\n"
				+ "   \"ExcludeDeletedIndicator\": \"false\",\r\n"
				+ "   \"AuditInfo\": {\r\n"
				+ "      \"UserId\": \"A1435477\",\r\n"
				+ "      \"UserName\": \"John Smith\",\r\n"
				+ "      \"Organization\": \"Any Bank\",\r\n"
				+ "      \"Phone\": \"5555551234\"\r\n"
				+ "   }\r\n"
				+ " }\r\n"
				+ "}\r\n"
				+ "}";
		
		Map<String,Object> map = new HashMap<String,Object>();
	    ObjectMapper mapper = new ObjectMapper();
	    map = mapper.readValue(y, new TypeReference<HashMap>(){});
	    
	    System.out.println(map);
	   
	    
	   Map<String, Object> requestMap = new HashMap<String, Object>();
	   requestMap.put("request", map);
	   
	   Splunk splunk =  Splunk.builder().key("key.test.teste")
	    				.starTime(OffsetDateTime.now())
	    				.endTime(OffsetDateTime.now().plusMinutes(1l))
	    				//.moreInfo(requestMap)
	    				//.request(map)
	    				.build();
	    
	   splunk.addMoreInfo(map);
	   
	    String trans = mapper2.writeValueAsString(splunk);
	    System.out.println("***");
	    System.out.println(trans);
	    System.out.println("***");
	    
	    
	    Map<String,Object> moreInfo = new HashMap<String,Object>();
	    
	    Map<String,Object> map2 = new HashMap<String,Object>();
	    map2.put("vl1", "valor1");
	    map2.put("vl2", "valor2");
	    
	    moreInfo.put("objet1", "VALOR OBJETO 1");
	    moreInfo.put("objet2", 123);
	    moreInfo.put("objet3", map2);
	    
	    splunk.addMoreInfo(moreInfo);
	    
	    trans = mapper2.writeValueAsString(splunk);
	    System.out.println(trans);
	    
	    
	}
}
