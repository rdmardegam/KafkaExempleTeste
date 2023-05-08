//package com.example.kafka.producer;
//
//import java.util.Optional;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.slf4j.Marker;
//
//import ch.qos.logback.classic.PatternLayout;
//import ch.qos.logback.classic.spi.ILoggingEvent;
//import ch.qos.logback.classic.spi.LoggingEvent;
//
//public class MaskingPatternLayout extends PatternLayout {
//
//	private String patternsProperty;
//	private Optional<Pattern> pattern;
//
//	public String getPatternsProperty() {
//		return patternsProperty;
//	}
//
//	public void setPatternsProperty(String patternsProperty) {
//		this.patternsProperty = patternsProperty;
//		if (this.patternsProperty != null) {
//			this.pattern = Optional.of(Pattern.compile(patternsProperty, Pattern.MULTILINE));
//		} else {
//			this.pattern = Optional.empty();
//		}
//	}
//
//	@Override
//	public String doLayout(ILoggingEvent event) {
//		final StringBuilder message = new StringBuilder(super.doLayout(event));
//
//		
//		LoggingEvent lEvent = (LoggingEvent) event;
//		
//		//lEvent.setMarker(net.logstash.logback.marker.Markers.appendRaw("payload", "RRR-123"));
//		
//		Marker m = event.getMarker();lEvent.getMarker();
//		m = net.logstash.logback.marker.Markers.empty();
//		System.out.println(m.toString());
//		/*Marker m = event.getMarker();
//		m = net.logstash.logback.marker.Markers.empty();*/
//		
//		
//		
//		if (pattern.isPresent()) {
//			Matcher matcher = pattern.get().matcher(message);
//			while (matcher.find()) {
//
//				int group = 1;
//				while (group <= matcher.groupCount()) {
//					if (matcher.group(group) != null) {
//						for (int i = matcher.start(group); i < matcher.end(group); i++) {
//							message.setCharAt(i, '*');
//						}
//					}
//					group++;
//				}
//			}
//		}
//		return message.toString();
//	}
//
//}
