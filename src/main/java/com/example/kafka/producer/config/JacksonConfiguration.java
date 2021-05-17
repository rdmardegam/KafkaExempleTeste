//package com.example.kafka.producer.config;
//
//import javax.ws.rs.ext.ContextResolver;
//import javax.ws.rs.ext.Provider;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.PropertyNamingStrategy;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
//
////@Configuration
////public class JacksonConfiguration {
////
////    @Autowired
////    private Jackson2ObjectMapperBuilder builder;
////
////    @Primary
////    @Bean
////    public ObjectMapper postConstruct() {
////        return this.builder
////                /*.serializers(new LocalDateSerializer(new DateTimeFormatterBuilder()
////                    .appendPattern("dd-MM-yyyy").toFormatter()))*/
////                /*.deserializers(new LocalDateDeserializer(new DateTimeFormatterBuilder()
////                    .appendPattern("dd/MM/yyyy").toFormatter())
////                		)*/
////                .serializationInclusion(JsonInclude.Include.NON_NULL)
////                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
////                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
////                .propertyNamingStrategy(PropertyNamingStrategy.SnakeCaseStrategy.SNAKE_CASE)
////                .build();
////    }
////    
////    
////    @Bean
////    public Formatter<LocalDate> localDateFormatter() {
////      return new Formatter<LocalDate>() {
////        @Override
////        public LocalDate parse(String text, Locale locale) throws ParseException {
////          return LocalDate.parse(text,  DateTimeFormatter.ofPattern("dd-MM-yyyy"));
////        }
////
////        @Override
////        public String print(LocalDate object, Locale locale) {
////          return DateTimeFormatter.ofPattern("dd-MM-yyyy").format(object);
////        }
////      };
////    }
////}
//
//@Provider
//public class JacksonConfiguration  implements ContextResolver<ObjectMapper> {  
//    private final ObjectMapper MAPPER;
//
//    public JacksonConfiguration() {
//        MAPPER = new ObjectMapper();
//        // Now you should use JavaTimeModule instead
//        MAPPER.registerModule(new JSR310Module());
//        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCaseStrategy.SNAKE_CASE);
//    }
//
//    @Override
//    public ObjectMapper getContext(Class<?> type) {
//        return MAPPER;
//    }  
//}