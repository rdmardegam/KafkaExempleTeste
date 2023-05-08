package com.example.kafka.producer;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication
@EnableKafka
@EnableAsync
@EnableAspectJAutoProxy
@EnableRetry
public class ProducerApplication extends RuntimeException implements ExitCodeGenerator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 690766885143727874L;

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}

	@Override
	public int getExitCode() {
		return 42;
	}
	
	
	@Bean("threadPoolTaskExecutor")
	public TaskExecutor  getAsyncExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(20);
	    executor.setMaxPoolSize(1000);
	    executor.setQueueCapacity(500);
	    executor.setWaitForTasksToCompleteOnShutdown(true);
	    executor.setThreadNamePrefix("Async-");
	    //executor.initialize();
	    return executor;
	  }
	
	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect(registry);
	}
	
//	@Bean
//	public static BeanPostProcessor bpp() { // static is important
//		return new BeanPostProcessor() {
//
//			@Override
//			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//				if (bean instanceof MasterCardConsumer) {
//					ProxyFactoryBean pfb = new ProxyFactoryBean();
//					pfb.setTarget(bean);
//					pfb.addAdvice(new MethodInterceptor() {
//
//						@Override
//						public Object invoke(MethodInvocation invocation) throws Throwable {
//							try {
//								System.out.println("Before");
//								return invocation.proceed();
//							} finally {
//								System.out.println("After");
//							}
//						}
//					});
//				}
//				return bean;
//			}
//		};
//	}
	
   
}