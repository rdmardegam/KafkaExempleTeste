package com.example.kafka.producer.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.logstash.logback.encoder.org.apache.commons.lang3.exception.ExceptionUtils;
import net.logstash.logback.marker.LogstashMarker;

import static net.logstash.logback.argument.StructuredArguments.*;

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
	
	public static void info3(Splunk splunk) {
		//logger.info(MarkerFactory.getMarker("messageSplunk"), prepareLog(splunk));
		MDC.put("RAMON_TAG", prepareLog(splunk));
		logger.info("", prepareLog(splunk));
		//logger.info("info", StructuredArguments.keyValue("RAMON_TAG",prepareLog(splunk)));
		MDC.remove("RAMON_TAG");
		
		
		//logger.info(net.logstash.logback.marker.Markers.appendRaw("message", prepareLog(splunk)), null);
		//logger.info(prepareLog(splunk)); 
	}
	
	public static void info2(Splunk splunk) {
		MDC.clear();
		prepareLog2(splunk);
		//logger.info("INFO2");
		//net.logstash.logback.marker.Markers.appendRaw("jsonMessage", prepareLog(splunk)).add(net.logstash.logback.marker.Markers.appendRaw("jsonMessage", prepareLog(splunk)))
		LogstashMarker l = net.logstash.logback.marker.Markers.appendRaw("jsonMessage", prepareLog(splunk));
		l.add(net.logstash.logback.marker.Markers.appendRaw("Besta", "123"));
		logger.info(l, "MOOONNNN");
		MDC.clear();
	}
	
	public static void info(Splunk splunk) {
		MDC.clear();
		logger.info(prepareLog3(splunk), splunk.getCustomMessage());
		MDC.clear();
	}
	
	public static void error(Splunk splunk) {
		MDC.clear();
		logger.error(prepareLog3(splunk), splunk.getCustomMessage());
		MDC.clear();
		
		//logger.error(prepareLog(splunk));
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
			//logString = logString.substring(1, logString.length() - 1);
			
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
	
	private static void prepareLog2(Splunk splunk) {
		String logString = null;
		try {
			if(splunk.getStarTime() != null && splunk.getEndTime() !=null && splunk.getElapseTime() == 0l) {
				Duration difference = Duration.between(splunk.getStarTime(),splunk.getEndTime());
				splunk.setElapseTime(difference.toMillis());
			}
			
			//logString = mapper.writeValueAsString(splunk);
			//logString = logString.substring(1, logString.length() - 1);
			
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
		
		MDC.put("key", splunk.getKey());
		MDC.put("customMessage", splunk.getCustomMessage());
		MDC.put("starTime", splunk.getStarTime().toString());
		try {
			MDC.put("moreInfo", mapper.writeValueAsString(splunk.getMoreInfo()));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static LogstashMarker prepareLog3(Splunk splunk) {
		LogstashMarker l = net.logstash.logback.marker.Markers.empty();
		
		try {
			if(splunk.getStarTime() != null && splunk.getEndTime() !=null && (splunk.getElapseTime() == null || splunk.getElapseTime() == 0l)) {
				Duration difference = Duration.between(splunk.getStarTime(),splunk.getEndTime());
				splunk.setElapseTime(difference.toMillis());
			}
			
			MDC.put("key", splunk.getKey());
			//MDC.put("message", splunk.getCustomMessage());
			
			if(splunk.getStarTime() != null) {
				MDC.put("starTime", splunk.getStarTime().toString());
			}
			
			if(splunk.getEndTime() != null) {
				MDC.put("endTime", splunk.getStarTime().toString());
			}
			
			if(splunk.getElapseTime() != null) {
				MDC.put("elapseTime", splunk.getElapseTime().toString());
			}
			
			
			if(splunk.getException() != null) {
				//StringWriter sw = new StringWriter();
				//splunk.getException().printStackTrace(new PrintWriter(sw));
				
				//MDC.put("exception", splunk.getException().getMessage().replaceAll("[^a-zA-Z0-9]", ""));
				//MDC.put("trace", sw.toString());
				splunk.addMoreInfo(Collections.singletonMap("exception", splunk.getException().toString()));
				//splunk.addMoreInfo(Collections.singletonMap("trace", sw.toString()));
				splunk.addMoreInfo(Collections.singletonMap("trace", ExceptionUtils.getStackTrace(splunk.getException())));
				
			}
			
			if(splunk.getPayload() != null) {
				if(splunk.getPayload() instanceof String) {
					MDC.put("payload", splunk.getPayload().toString());
				} else {
					l.add(net.logstash.logback.marker.Markers.appendRaw("payload", mapper.writeValueAsString(splunk.getPayload())));
				}
			}
			
			if(splunk.getMoreInfo() != null) {
				splunk.getMoreInfo().forEach((k,v) -> {
					try {
						l.add(net.logstash.logback.marker.Markers.appendRaw(k, mapper.writeValueAsString(v)));
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
			
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
		
		return l;
		
	}
	
}
