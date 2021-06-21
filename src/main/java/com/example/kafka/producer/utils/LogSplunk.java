package com.example.kafka.producer.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.splunk.logging.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class LogSplunk {
	private static Logger logger = LoggerFactory.getLogger("splunk");
	
	private static ObjectMapper mapper;
	static {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
	
	public static void info(Splunk splunk) {
		logger.info(prepareLog(splunk));
	}
	
	public static void error(Splunk splunk) {
		logger.error(prepareLog(splunk));
	}
	
	public static void debug(Splunk splunk) {
		logger.debug(prepareLog(splunk));
	}
	
	public static void warn(Splunk splunk) {
		logger.warn(prepareLog(splunk));
	}
	
	private static String prepareLog(Splunk splunk) {
		String logString = null;
		try {
			if(splunk.getStarTime() != null && splunk.getEndTime() !=null && splunk.getElapseTime() == 0l) {
				Duration difference = Duration.between(splunk.getStarTime(),splunk.getEndTime());
				splunk.setElapseTime(difference.toMillis());
			}
			
			logString = mapper.writeValueAsString(splunk);
			logString = logString.substring(1, logString.length() - 1);
			
			if(splunk.getException() != null) {
				StringWriter sw = new StringWriter();
				splunk.getException().printStackTrace(new PrintWriter(sw));
				
				logString += ",\"exception\":"; 
				logString += mapper.writeValueAsString(splunk.getException().toString());;
								
				logString += ",\"trace\":"; 
				logString += mapper.writeValueAsString(sw.toString());
				
			}
			
		} catch (JsonProcessingException e) {
			logString = "";
		}

		return logString;
	}
	
}
