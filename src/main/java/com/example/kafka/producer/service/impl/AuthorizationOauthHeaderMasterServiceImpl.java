package com.example.kafka.producer.service.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.example.kafka.producer.exception.TechnicalException;
import com.example.kafka.producer.service.AuthorizationOauthHeaderService;
import com.mastercard.developer.oauth.OAuth;
import com.mastercard.developer.utils.AuthenticationUtils;

@Service
public class AuthorizationOauthHeaderMasterServiceImpl implements AuthorizationOauthHeaderService {
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 1567290845550587823L;
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationOauthHeaderMasterServiceImpl.class);

	@Value("${certificadoMaster.fileCertificateLocation}")
	private String fileCertificateLocation;
	
	@Value("${certificadoMaster.signingKeyAlias}")
	private String signingKeyAlias;
	 
	@Value("${certificadoMaster.signingKeyPassword}")
	private String  signingKeyPassword;
	
	@Value("${certificadoMaster.consumerKey}")
	private String  consumerKey;
	
	
	@Override
	public String gerarHeaderToken(String url, String payload, HttpMethod method) {
		String header = "";
		try {

			 PrivateKey signingKey = AuthenticationUtils.loadSigningKey(fileCertificateLocation, 
					 	 signingKeyAlias, 
					 	 signingKeyPassword);
			
			URI uri = URI.create(url);
			Charset charset = StandardCharsets.UTF_8;

			header = OAuth.getAuthorizationHeader(uri, method.toString(), payload, charset, consumerKey, signingKey);
			logger.info("HEADER: {}", header);

		} catch (UnrecoverableKeyException | KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
			throw new TechnicalException("Problema com o certificado ou geração do Header", e);
		}
		
		return header;
	}
}