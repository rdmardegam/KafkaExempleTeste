package com.example.kafka.producer.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.example.kafka.producer.model.MasterCardDTO;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.logstash.logback.encoder.org.apache.commons.lang3.exception.ExceptionUtils;
import net.logstash.logback.marker.LogstashMarker;

public class LogSplunk {
	
	private static Logger logger = LoggerFactory.getLogger("splunk");
	private static Logger loggerSteps = LoggerFactory.getLogger("splunkSteps");
	
	private static ObjectMapper mapper;
	static {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	// Static info
	private static String uniqueKey;
	private static String uniqueKeyExecution;
	private Set<Object> root;	
	
	
	public static Map<Long, String[]> uniqueKeyMap = Collections.synchronizedMap(new HashMap<Long, String[]>());
	public static Map<Long, List<Splunk>> stepsThead = Collections.synchronizedMap(new HashMap<Long, List<Splunk>>());
	
	public static void initLog(String uniqueKey, String uniqueKeyExecution, String topicName) {
		uniqueKeyMap.put(Thread.currentThread().getId(), new String[]{uniqueKey, uniqueKeyExecution, topicName});
		stepsThead.put(Thread.currentThread().getId(), new ArrayList<Splunk>());
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
			
			if(uniqueKeyMap.containsKey(Thread.currentThread().getId())) {
				MDC.put("uniqueKey", uniqueKeyMap.get(Thread.currentThread().getId())[0]);
				MDC.put("uniqueExecutionKey", uniqueKeyMap.get(Thread.currentThread().getId())[1]);
				MDC.put("topico", uniqueKeyMap.get(Thread.currentThread().getId())[2]);
			}
			
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
				//splunk.addMoreInfo(Collections.singletonMap("trace", sw.toString()));
				splunk.addMoreInfo(Collections.singletonMap("exception", splunk.getException().toString()));
				
				Map<String, Object> mapError = new HashMap<String, Object>();
				mapError.put("erroMsg", Collections.singletonMap("error", splunk.getException().toString()));
				mapError.put("trace", Collections.singletonMap("trace", ExceptionUtils.getStackTrace(splunk.getException())));
				
				splunk.addMoreInfo(Collections.singletonMap("error", mapError));
				
			}
			
			if(splunk.getPayload() != null) {
				//splunk.addMoreInfo(Collections.singletonMap("payload", splunk.getPayload()));
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
			
			stepsThead.get(Thread.currentThread().getId()).add(splunk);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return l;
	}
	
	
	public static void finalizeLog() throws JsonProcessingException {
		MDC.clear();
		List<Map<String, Object>> mapLogs = new ArrayList<Map<String, Object>>();
		LogstashMarker l = net.logstash.logback.marker.Markers.empty();
		
		
		List<Splunk> listSplunk = stepsThead.get(Thread.currentThread().getId());
		
		int [] index = new int [1] ; 
		
		listSplunk.forEach(item-> {
			Map<String, Object> stepMap = new HashMap<String, Object>();
			Map<String, Object> flatenedMap = new HashMap<String, Object>();
			Map<String, Object> map = mapper.convertValue(item, Map.class);
			
			stepMap.put("_step", index[0]++);
						
			map.forEach((key, value) -> {
			    	if(!key.equals("payload")) {
				    	if(value instanceof Map) {
					        flatenedMap.putAll((Map) value);
					      } else {
					        flatenedMap.put(key, value);
					      }
				    } else {
				    	flatenedMap.put(key, value);
				    }
			    }  
			);
			
			stepMap.put("conteudo", flatenedMap);
			mapLogs.add(stepMap);
		});
		
		Splunk lastLog = listSplunk.get(listSplunk.size()-1);
		// Add ultimo log na raiz
		l = prepareLog3(lastLog);
		// add lista dos steps
		l.add(net.logstash.logback.marker.Markers.appendRaw("steps", mapper.writeValueAsString(mapLogs)));
		
		// Log 
		if(lastLog.getException() != null) {
			loggerSteps.error(l,lastLog.getCustomMessage());	
		} else {
			loggerSteps.info(l,lastLog.getCustomMessage());
		}
		
		MDC.clear();
	}
	
	
	public static void main(String[] args) {
		MasterCardDTO masterCardDTO = new MasterCardDTO();
		masterCardDTO.setNumeroCartao("12313456");
		masterCardDTO.setCorrelationId("asd5as1da2s1d32");
		masterCardDTO.setDataEvento(OffsetDateTime.now());
		
		//618 GOOGLE
		//masterCardDTO.setNumeroCarteira(List.of("216","217","103","618").get(new Random().nextInt(4)));
		masterCardDTO.setNumeroCarteira("618");
		
		masterCardDTO.setIdCartao(UUID.randomUUID().toString());
		masterCardDTO.setIdDispositivo(UUID.randomUUID().toString());
		
		//masterCardDTO.setCodigoBandeira(List.of("M","V").get(new Random().nextInt(2)));
		masterCardDTO.setCodigoBandeira("M");
		
		masterCardDTO.setTipoCartao(List.of("D","M", "C").get(new Random().nextInt(3)));
		masterCardDTO.setOrigemInformacao(List.of("BC","VQ").get(new Random().nextInt(2)));
		
		mask(masterCardDTO);
		System.out.println(masterCardDTO.toString());
		
		
		String a = "MasterCardDTO(idCartao=9b7fb87d-e226-4a45-a3fd-6211554db3fc, numeroCartao=12313456, correlationId=asd5as1da2s1d32, numeroCarteira=XXX, codigoBandeira=M, tipoCartao=M, origemInformacao=VQ, idDispositivo=72c72ae4-1653-4949-9d01-7282e6d809b8, dataEvento=2021-07-01T00:02:05.342387500-03:00)";
		
		System.out.println("numeroCartao".contains("numeroCarta"));

		/*Map<String, Object> map = new HashMap<String, Object>();
		map.put("numeroCartao", "123456787");
		map.put("numeroCarteira", masterCardDTO);
		mask(map);*/
	}
	
	static Set fieldSet = new HashSet();
	static List<String> fieldNames = Arrays.asList("numeroCa", "numeroCarteira");
	
	 public static void mask(Object object) {
	        try {
	            Field[] fields = object.getClass().getDeclaredFields();
	            Object value = null;
	            for (int i = 0; i < fields.length; i++) {
	                fields[i].setAccessible(true);
	                value = fields[i].get(object);
	                if (value != null) {
	                    if (fields[i].getType().isArray()
	                            || fields[i].getType().getCanonicalName().startsWith("com.mask.json")) {
	                        mask(value);
	                    } else {
	                    	Field fieldxx = fields[i];
	                        
	                    	boolean exist = fieldNames.stream()
	                    			.anyMatch(f-> {
	                    				
	                    					System.out.println(
	                    							fieldxx.getName() + " - " + f + " - "+
	                    									fieldxx.getName().toLowerCase().contains(f.toLowerCase()));
	                    					
	                    					return fieldxx.getName().toLowerCase().contains(f.toLowerCase());
	                    				} 
	                    			);
	                    	
	                    	if (exist
	                        		&& fieldxx.get(object) != null) {
	                    		fieldxx.set(object, replaceDigits((String) fieldxx.get(object)));
	                        }
	                    }
	                }
	 
	            }
	        } catch (IllegalAccessException ex) {
	            ex.printStackTrace();
	        }
	    }
	 
	    private static String replaceDigits(String text) {
	        StringBuffer buffer = new StringBuffer(text.length());
	        Pattern pattern = Pattern.compile("\\d");
	        Matcher matcher = pattern.matcher(text);
	        while (matcher.find()) {
	            matcher.appendReplacement(buffer, "Z");
	        }
	        return buffer.toString();
	    }

		public static String getUniqueKey() {
			return uniqueKey;
		}

		public static void setUniqueKey(String uniqueKey) {
			LogSplunk.uniqueKey = uniqueKey;
		}

		public static String getUniqueKeyExecution() {
			return uniqueKeyExecution;
		}

		public static void setUniqueKeyExecution(String uniqueKeyExecution) {
			LogSplunk.uniqueKeyExecution = uniqueKeyExecution;
		}

		public Set<Object> getRoot() {
			return root;
		}

		public void setRoot(Set<Object> root) {
			this.root = root;
		}
}