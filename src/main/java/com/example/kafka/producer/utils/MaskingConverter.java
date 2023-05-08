package com.example.kafka.producer.utils;

import com.fasterxml.jackson.core.JsonStreamContext;

import net.logstash.logback.mask.ValueMasker;



public class MaskingConverter implements  ValueMasker {

	@Override
	public Object mask(JsonStreamContext context, Object value) {
		// TODO Auto-generated method stub
		
		//System.out.println("V" + value);
		if ("message".equals(context.getCurrentName()) && value instanceof CharSequence) {
            //return maskMiddle(value.toString());
			return value.toString();
        }
		return value;
	}
	
	
	private String maskMiddle(String content) {
		int contentLength = content.length();
		int startIndex = contentLength / 3;
		int endIndex = contentLength - startIndex;
		StringBuilder builder = new StringBuilder(content);
		for (int i = startIndex; i < endIndex; i++) {
			builder.setCharAt(i, '*');
		}
		return builder.toString();
	}
    
}
//
//public class MaskingConverter extends ClassicConverter {
//    @Override
//    public String convert(ILoggingEvent event) {
//        String message = event.getMessage();
//        // Implemente o comportamento de mascaramento dos valores
//        // de acordo com as chaves dos campos do JSON
//        
////        String maskedMessage = maskMiddle(message);
////        return maskedMessage;
//        
//        
//        
//        return message;
//        
//    }
//    
//    private String maskMiddle(String content) {
//        int contentLength = content.length();
//        int startIndex = contentLength / 3;
//        int endIndex = contentLength - startIndex;
//        StringBuilder builder = new StringBuilder(content);
//        for (int i = startIndex; i < endIndex; i++) {
//            builder.setCharAt(i, '*');
//        }
//        return builder.toString();
//    }
//}
