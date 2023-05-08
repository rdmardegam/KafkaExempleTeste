package com.example.kafka.producer.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.xml.bind.DatatypeConverter;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;

public class EncryptionTest2 {

	public static void main(String[] args)  throws Exception {
		// RSA signatures require a public and private RSA key pair,
		// the public key must be made known to the JWS recipient in
		// order to verify the signatures
		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
		keyGenerator.initialize(2048);

		KeyPair kp = keyGenerator.genKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey)kp.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey)kp.getPrivate();

		// Create RSA-signer with the private key
		JWSSigner signer = new RSASSASigner(privateKey);

		// Prepare JWS object with simple string as payload
		JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.PS256), new Payload("In RSA we trust!"));

		// Compute the RSA signature
		jwsObject.sign(signer);

		
		
		assertTrue(jwsObject.getState().equals(JWSObject.State.SIGNED));

		// To serialize to compact form, produces something like
		// eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
		// mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
		// maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
		// -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
		String s = jwsObject.serialize();

		System.out.println(s);
		System.out.println( DatatypeConverter.printBase64Binary(publicKey.getEncoded()));
		
		// To parse the JWS and verify it, e.g. on client-side
		jwsObject = JWSObject.parse(s);

		JWSVerifier verifier = new RSASSAVerifier(publicKey);

		System.out.println(jwsObject.verify(verifier));

		System.out.println(("In RSA we trust!".equals(jwsObject.getPayload().toString())));
		

	}
	
	
}
