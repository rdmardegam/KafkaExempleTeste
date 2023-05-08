//package com.example.kafka.producer.config;
//
//import java.io.IOException;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//
//@Component
//public class MDCLogFilter extends GenericFilterBean {
//	
//	  private static final Logger logger = LoggerFactory.getLogger(MDCLogFilter.class);
//	  @Override
//	  public void destroy() {
//	  }
//	  @Override
//	  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
//	      FilterChain filterChain) throws IOException, ServletException {
//	    MDC.put("context.userId", "amarutha");
//	    MDC.put("context.moduleId", "CoreApp1");
//	
//	    MDC.put("context.caseId", "Case001");
//	    logger.trace("A TRACE Message");
//	    logger.debug("A DEBUG Message");
//	    logger.info("An INFO Message");
//	    logger.warn("A WARN Message");
//	    logger.error("An ERROR Message");
//	    filterChain.doFilter(servletRequest, servletResponse);
//	  }
//	}