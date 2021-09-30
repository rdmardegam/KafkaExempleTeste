/*
 *
 *   © Copyright 2018 - 2020 Visa. All Rights Reserved.
 *
 *   NOTICE: The software and accompanying information and documentation (together, the “Software”) remain the property of and are proprietary to Visa
 *   and its suppliers and affiliates. The Software remains protected by intellectual property rights and may be covered by U.S. and foreign patents or patent applications.
 *   The Software is licensed and not sold.
 *
 *  By accessing the Software you are agreeing to Visa's terms of use (developer.vis.com/terms) and privacy policy (developer.visa.com/privacy).
 *  In addition, all permissible uses of the Software must be in support of Visa products,
 *  programs and services provided through the Visa Developer Program (VDP) platform only (developer.visa.com).
 *  **THE SOFTWARE AND ANY ASSOCIATED INFORMATION OR DOCUMENTATION IS PROVIDED ON AN “AS IS,” “AS AVAILABLE,” “WITH ALL FAULTS” BASIS WITHOUT WARRANTY OR  CONDITION OF ANY KIND. YOUR USE IS AT YOUR OWN RISK.**
 *  All brand names are the property of their respective owners, used for identification purposes only,
 *  and do not imply product endorsement or affiliation with Visa. Any links to third party
 *  sites are for your information only and
 *  equally  do not constitute a Visa endorsement. Visa has no insight into and control over
 *  third party content and
 *  code and disclaims all liability for any such components, including continued availability
 *  and functionality.
 *  Benefits depend on implementation details and business factors and coding steps shown are exemplary only and
 *  do not reflect all necessary elements for the described capabilities. Capabilities and
 *  features are subject to Visa’s terms and conditions and
 *  may require development,implementation and resources by you based on your business
 *  and operational details.
 *  Please refer to the specific API documentation for details on the requirements, eligibility
 *  and geographic availability.
 *
 *  This Software includes programs, concepts and details under continuing development by
 *  Visa. Any Visa features,functionality, implementation, branding, and
 * schedules may be amended, updated or canceled at Visa’s discretion.
 *  The timing of widespread availability of programs and functionality is also subject to a number of factors outside Visa’s control,including but
 *  not limited to deployment of necessary infrastructure by issuers, acquirers, merchants
 *  and mobile device manufacturers.
 *
 *  This sample code is licensed only for use in a non-production environment for sandbox testing. See the license for all terms of use.
 */
package com.example.kafka.producer.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.junit.Test;

import com.example.kafka.producer.teste.EncryptionUtils;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;

/**
 * @author speerbuc@visa.com
 **/
public class EncryptionUtilsTest {

    private static final Logger LOGGER = Logger.getLogger(EncryptionUtilsTest.class.getName());

    private static final String PLAIN_TEXT = "This is a plain text";

    @Test
    public void createAndDecryptJweTestUsingSharedSecret() throws Exception {
       /* //Generate Random API Key
        String apiKey = UUID.randomUUID().toString();
        LOGGER.info("Generated API Key: " + apiKey);

        //Generate Shared Secret
        String sharedSecret = UUID.randomUUID().toString();
        LOGGER.info("Generated Shared Secret: " + apiKey);*/
        String apiKey = "X_API_KEY_TESTE";
    	String sharedSecret = "11asda4sd65104389e44651vs56cv894e45";
        
        
        Map<String, Object> jweHeaders = new HashMap<String, Object>();
        jweHeaders.put("iat", System.currentTimeMillis());

        String jwe = EncryptionUtils.createJwe(PLAIN_TEXT, apiKey, sharedSecret, JWEAlgorithm.A256GCMKW, EncryptionMethod.A256GCM, jweHeaders);
        LOGGER.info("Generated JWE: " + jwe);
        assertNotNull(jwe);

        verifyGeneratedJweHas5Parts(jwe);
        verifyGeneratedJweContainsAllHeaders(jwe, apiKey, jweHeaders);

        Map<String, Object> jwsHeaders = new HashMap<String, Object>();
        long iat = System.currentTimeMillis() / 1000;
        Long exp = iat + 120;
        jwsHeaders.put("iat", iat);
        jwsHeaders.put("exp", exp);
        
        String jws = EncryptionUtils.createJws(jwe, apiKey, sharedSecret, jwsHeaders);
        LOGGER.info("Generated JWS: " + jws);
        verifyGeneratedJwsHas3Parts(jws);
        verifyGeneratedJwsContainsAllHeaders(jws, null, jwsHeaders);

        String jweFromJws = EncryptionUtils.verifyAndExtractJweFromJWS(jws, sharedSecret);
        assertEquals(jwe, jweFromJws);

        String decryptedJWE = EncryptionUtils.decryptJwe(jweFromJws, sharedSecret);
        assertEquals(PLAIN_TEXT, decryptedJWE);
    }

    @Test
    public void createAndDecryptJweTestUsingSharedSecretWithXmlPayload() throws Exception {
        String payload = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><request></request>";
        //Generate Random API Key
        String apiKey = UUID.randomUUID().toString();
        LOGGER.info("Generated API Key: " + apiKey);

        //Generate Shared Secret
        String sharedSecret = UUID.randomUUID().toString();
        LOGGER.info("Generated Shared Secret: " + apiKey);

        Map<String, Object> jweHeaders = new HashMap<String, Object>();
        jweHeaders.put("iat", System.currentTimeMillis());

        String jwe = EncryptionUtils.createJweWithXmlPayload(payload, apiKey, sharedSecret,
                JWEAlgorithm.A256GCMKW, EncryptionMethod.A256GCM, jweHeaders);
        LOGGER.info("Generated JWE: " + jwe);
        assertNotNull(jwe);
        verifyGeneratedJweHas5Parts(jwe);

        String b64EncodedHeader = jwe.split("\\.")[0];
        JWEHeader jweHeader = JWEHeader.parse(Base64URL.from(b64EncodedHeader));
        LOGGER.info("JWE Header: " + jweHeader.toString());

        assertEquals("application/xml", jweHeader.getContentType());

        String decryptedJWE = EncryptionUtils.decryptJwe(jwe, sharedSecret);
        assertEquals(payload, decryptedJWE);
    }

 /*   @Test
    public void createAndDecryptJweTestUsingRSAPKI() throws Exception {
        //Generate a random key pair
        KeyPair keyPair = generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        LOGGER.info("Generated Private Key: " + DatatypeConverter.printBase64Binary(privateKey.getEncoded()));

        PublicKey publicKey = keyPair.getPublic();
        LOGGER.info("Generated Public Key: " + DatatypeConverter.printBase64Binary(publicKey.getEncoded()));

        //Generate Random KID
        String kid = UUID.randomUUID().toString();
        LOGGER.info("Kid: " + kid);

        Map<String, Object> jweHeaders = new HashMap<String, Object>();
        jweHeaders.put("iat", System.currentTimeMillis());

        String jwe = EncryptionUtils.createJwe(PLAIN_TEXT, kid, (RSAPublicKey) publicKey, JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM, jweHeaders);
        assertNotNull(jwe);

        verifyGeneratedJweHas5Parts(jwe);
        verifyGeneratedJweContainsAllHeaders(jwe, kid, jweHeaders);

        Map<String, Object> jwsHeaders = new HashMap<String, Object>();
        long iat = System.currentTimeMillis() / 1000;
        Long exp = iat + 120;
        jwsHeaders.put("iat", iat);
        jwsHeaders.put("exp", exp);

        String signingKid = UUID.randomUUID().toString();
        String jws = EncryptionUtils.createJws(jwe, signingKid, (RSAPrivateKey) privateKey, jwsHeaders);
        LOGGER.info("Generated JWS: " + jws);
        verifyGeneratedJwsHas3Parts(jws);
        verifyGeneratedJwsContainsAllHeaders(jws, signingKid, jwsHeaders);

        String jweFromJws = EncryptionUtils.verifyAndExtractJweFromJWS(jws, (RSAPublicKey) publicKey);
        assertEquals(jwe, jweFromJws);

        String decryptedJWE = EncryptionUtils.decryptJwe(jweFromJws, (RSAPrivateKey) privateKey);
        assertEquals(PLAIN_TEXT, decryptedJWE);
    }*/

    private void verifyGeneratedJweHas5Parts(String jwe) {
        assertEquals(5, jwe.split("\\.").length);
    }

    private void verifyGeneratedJweContainsAllHeaders(String jwe, String kid, Map<String, Object> headers) throws ParseException {
        String b64EncodedHeader = jwe.split("\\.")[0];
        JWEHeader jweHeader = JWEHeader.parse(Base64URL.from(b64EncodedHeader));
        LOGGER.info("JWE Header: " + jweHeader.toString());
        assertEquals(kid, jweHeader.getKeyID());

        for (String k : headers.keySet()) {
            assertEquals(headers.get(k), jweHeader.getCustomParam(k));
        }
    }

    private void verifyGeneratedJwsHas3Parts(String jws) {
        assertEquals(3, jws.split("\\.").length);
    }

    private void verifyGeneratedJwsContainsAllHeaders(String jws, String kid, Map<String, Object> headers) throws Exception {
        String b64EncodedHeader = jws.split("\\.")[0];
        JWSHeader jwsHeader = JWSHeader.parse(Base64URL.from(b64EncodedHeader));
        LOGGER.info("JWS Header: " + jwsHeader.toString());
        assertEquals(kid, jwsHeader.getKeyID());

        for (String k : headers.keySet()) {
            assertEquals(headers.get(k), jwsHeader.getCustomParam(k));
        }
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(4096);
        return kpg.generateKeyPair();
    }

}