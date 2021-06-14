//package com.example.kafka.producer.utils;
//
//import java.time.OffsetDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.UUID;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//
//import lombok.Builder;
//import lombok.Getter;
//
//@Builder(builderMethodName = "internalBuilder")
//@Getter
//public class LogSplunk_old {
//
//	@JsonIgnore
//	private static Logger logger = LoggerFactory.getLogger("splunk");
//
//	@JsonIgnore
//	private static ObjectMapper mapper;
//	static {
//		mapper = new ObjectMapper();
//		mapper.registerModule(new JavaTimeModule());
//		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
//		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//	}
//
//	@Builder.Default
//	private String id = UUID.randomUUID().toString();
//
//	private String key;
//
//	@Builder.Default
//	private String time = OffsetDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
//
//	private String exception;
//
//	@Builder.Default
//	private TypeLog type = TypeLog.INFO;
//
//	private String customMessage;
//
//	private Object payload;
//
//	public static LogSplunkBuilder builder(TypeLog typeLog) {
//		return internalBuilder().type(typeLog);
//	}
//
//	public static LogSplunkBuilder builder() {
//		return internalBuilder().type(TypeLog.INFO);
//	}
//
//	@Getter
//	public enum TypeLog {
//		INFO("info"), ERROR("error"), DEBUG("debug"), TRACE("trace"), WARNING("warning");
//
//		String type;
//
//		TypeLog(String type) {
//			this.type = type;
//		}
//	}
//
//	public void sendLog() {
//		String logString = "";
//		try {
//
//			logString = mapper.writeValueAsString(this);
//
//			// Remove chaves internas e externas
//			logString = logString.substring(1, logString.length() - 1);
//
//			// efetua o logg da aplicacao
//			switch (this.type) {
//				case INFO: {
//					logger.info(logString);
//					break;
//				}
//				case ERROR: {
//					logger.error(logString);
//					break;
//				}
//				case DEBUG: {
//					logger.debug(logString);
//					break;
//				}
//				case TRACE: {
//					logger.debug(logString);
//					break;
//				}
//				case WARNING: {
//					logger.warn(logString);
//					break;
//				}
//				default: {
//					logger.info(logString);
//				}
//			}
//
//		} catch (JsonProcessingException e) {
//			logger.error("Erro ao criar menssagem para o Splunk", e);
//		}
//	}
//}
