package com.example.kafka.producer.config;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Collections;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateCertificadoConfig {
	
	private static final Logger log = LoggerFactory.getLogger(RestTemplateCertificadoConfig.class);
	
	@Value("${certificadoMaster.fileCertificateLocation}")
	private String fileCertificateLocation;
	
	@Value("${certificadoMaster.signingKeyPassword}")
	private String signingKeyPassword;
	
	@Value("${master.base.url}")
	private String baseUrlApiMasterCard;
	

	@Autowired
	RestTemplateInterceptor restTemplateInterceptor;
	
	@Bean
	public RestTemplate restTemplate() throws Exception {
		KeyStore clientStore = KeyStore.getInstance("PKCS12");
		clientStore.load(new FileInputStream(fileCertificateLocation), signingKeyPassword.toCharArray());
		
	    SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
	    sslContextBuilder.setProtocol("TLS");
	    sslContextBuilder.loadKeyMaterial(clientStore, signingKeyPassword.toCharArray());
	    sslContextBuilder.loadTrustMaterial(new TrustSelfSignedStrategy());
		
	    SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
	    
	    
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(50);// Max connection
		connectionManager.setDefaultMaxPerRoute(10); // Max Per Route
//		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.custom().setConnectionManager(connectionManager).build()));
//		template.setInterceptors( Collections.singletonList(requestResponseLoggingInterceptor));
		
		CloseableHttpClient client = HttpClientBuilder.create().
									 setConnectionManager(connectionManager).
									 setSSLSocketFactory(sslConnectionSocketFactory)
									 .build();
	    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
		
	    
	    RestTemplate template = new RestTemplate(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
		template.setInterceptors( Collections.singletonList(restTemplateInterceptor));
		template.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrlApiMasterCard));
		
		log.info("[RestTemplate] - Iniciado");
		 
		return template;
	}
	
	
//	@Bean
//	public RestTemplate restTemplate() {
//		RestTemplate template = new RestTemplate();
//		
//		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
//		connectionManager.setMaxTotal(100);
//		connectionManager.setDefaultMaxPerRoute(6);
//		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.custom().setConnectionManager(connectionManager).build()));
//		
//		//template.setInterceptors( Collections.singletonList(new RequestResponseLoggingInterceptor()));
//		
//		System.out.println("RESTTEMPLATE INIT");
//		
//		return template;
//	}
	
//	@Bean
//	public RestTemplate restTemplate() throws Exception {
//	    KeyStore clientStore = KeyStore.getInstance("PKCS12");
//	    clientStore.load(new FileInputStream("/path/to/certfile"), "certpassword".toCharArray());

//		KeyStore clientStore = KeyStore.getInstance(new File("./keyalias-sandbox.p12"),"keystorepassword1".toCharArray());
//
//	    SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
//	    sslContextBuilder.useProtocol("TLS");
//	    sslContextBuilder.loadKeyMaterial(clientStore, "certpassword".toCharArray());
//	    //sslContextBuilder.loadTrustMaterial(new TrustSelfSignedStrategy());
//
//	    SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
//	    CloseableHttpClient httpClient = HttpClients.custom()
//	            .setSSLSocketFactory(sslConnectionSocketFactory)
//	            .build();
//	    
//	    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//	    requestFactory.setConnectTimeout(10000); // 10 seconds
//	    requestFactory.setReadTimeout(10000); // 10 seconds
//	    
//	    return new RestTemplate(requestFactory);

//		
//		System.out.println("CARREGANDO CERTIFICADO");
//		
//		KeyStore clientStore = KeyStore.getInstance("PKCS12");
//	    clientStore.load(new FileInputStream("./keyalias-sandbox.p12"), "keystorepassword1".toCharArray());
//
//	    SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
//	    sslContextBuilder.useProtocol("TLS");
//	    sslContextBuilder.loadKeyMaterial(clientStore, "keystorepassword1".toCharArray());
//	    //sslContextBuilder.loadTrustMaterial(clientStore);
//
//	    SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
//	    CloseableHttpClient httpClient = HttpClients.custom()
//	            .setSSLSocketFactory(sslConnectionSocketFactory)
//	            .build();
//	    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//	    return new RestTemplate(requestFactory);

//	}

//	@Bean
//	public HttpClient httpClient(SSLContext sslContext) {
//		return HttpClients.custom().setMaxConnPerRoute(30).setMaxConnTotal(60).setSSLContext(sslContext).build();
//	}
//
//	@Bean
//	public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
//		return new HttpComponentsClientHttpRequestFactory(httpClient);
//	}
//
//	@Bean
//	public RestTemplate restOperations(ClientHttpRequestFactory clientHttpRequestFactory) throws Exception {
//		System.out.println("CARREGANDO RESTTEMPLATE");
//		return new RestTemplate(clientHttpRequestFactory);
//	}
//
//	@Autowired
//	private ResourceLoader resourceLoader;
//
//	@Bean
//	public SSLContext sslContext() throws Exception {
//		
//		
//		// load the keystore file as input stream
//		InputStream keystoreStream = resourceLoader.getResource("./keyalias-sandbox.p12").getInputStream();
//
//		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//		// get instance of keystore depends on type of store here we have //pkcs12
//		KeyStore keyStore = KeyStore.getInstance("PKCS12");
//		try {
//			keyStore.load(keystoreStream, "keystorepassword1".toCharArray());
//		} finally { 
//			keystoreStream.close();
//		}
//		keyManagerFactory.init(keyStore, "keystorepassword1".toCharArray());
//		KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
//		// load trust store file as input stream
//		KeyStore trustStore = KeyStore.getInstance("PKCS12");
//		InputStream truststoreSteam = resourceLoader.getResource("./keyalias-sandbox.p12").getInputStream();
//
//		try {
//			// load trust store
//			trustStore.load(truststoreSteam, "keystorepassword1".toCharArray());
//		} finally {
//			truststoreSteam.close();
//		}
//		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//		tmf.init(trustStore);
//
//		SSLContext sslcontext = SSLContext.getInstance("TLS");
//
//		// initialize ssl context with both key manager and trust manager
//		sslcontext.init(keyManagers, tmf.getTrustManagers(), null);
//		 
//		System.out.println("CARREGANDO SSLW");
//		
//		// return sslContext
//		return sslcontext; 
//	}

	
	
	
	

//	private String getHeaderOath() throws Exception {
//		PrivateKey signingKey = AuthenticationUtils.loadSigningKey("./keyalias-sandbox.p12", "keyalias","keystorepassword1");
//		HttpHeaders header = new HttpHeaders();
//		URI uri = new URI("https://api.mastercard.com/service");
//		
//		HttpRequest request = new HttpRequest() {
//			@Override
//			public HttpMethod getMethod() {
//				return HttpMethod.POST;
//			}
//
//			@Override
//			public String getMethodValue() {
//				return getMethod().toString();
//			}
//
//			@Override
//			public URI getURI() {
//				return uri;
//			}
//
//			@Override
//			public HttpHeaders getHeaders() {
//				return header;
//			}
//		};
//
//		SpringHttpRequestSigner instanceUnderTest = new SpringHttpRequestSigner(
//				"6ynVPNsWkA59SZXvieIRJ7tbapzylu9jxaq3te4re9dca799!5f9bae87b9fa4b6faee7cfacfdce5e9f0000000000000000",
//				signingKey);
//		instanceUnderTest.sign(request, null);
//
//		System.out.println(header.getFirst(HttpHeaders.AUTHORIZATION));
//
//		return header.getFirst(HttpHeaders.AUTHORIZATION);
//
//	}

	
}
