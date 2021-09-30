package com.example.kafka.producer.teste;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.EncryptedJWT;

public class TesteVisaEncrypt {

	static String apiKey = "X_API_KEY_TESTE";
	static String sheredSecredt = "11asda4sd65104389e44651vs56cv894e45";
	
	public static void main(String[] args) throws Exception {
		
		
		String dataToEncrypt = "{\"pan\": \"1223131465465\" }";
		
		String jwe = createJwe(dataToEncrypt, apiKey, sheredSecredt);
		String jws = createJws(jwe, sheredSecredt);
		
		System.out.println("JWE:"+ jwe);
		System.out.println("JWS:"+ jws);
		
		System.out.println("\n");
		//System.out.println(decryptJwe(jwe, sheredSecredt));
		System.out.println(verifyAndExtractJweFromJWS(jws, sheredSecredt));
		System.out.println(decryptJwe(verifyAndExtractJweFromJWS(jws, sheredSecredt), sheredSecredt));
		
	}
	
	
	/**
	 * Create JWE Using Shared Secret
	 *
	 * @param plainText - Plain Text to encrypt
	 * @param apiKey - Key ID (API Key)
	 * @param secretSecret - Shared Secret
	 * @return JWE String in Compact Serialization Format
	 * @throws Exception
	 */
	public static String createJwe(String plainText, String apiKey, String sharedSecret) throws Exception {

		JWEHeader.Builder headerBuilder = new JWEHeader.Builder(JWEAlgorithm.A256GCMKW, EncryptionMethod.A256GCM);
		headerBuilder.keyID(apiKey);
		headerBuilder.customParam("iat", System.currentTimeMillis());
		//ADD CUSTOM
		//headerBuilder.customParam("typ", "JOSE");
		
		JWEObject jweObject = new JWEObject(headerBuilder.build(), new Payload(plainText));
		jweObject.encrypt(new AESEncrypter(sha256(sharedSecret)));
		return jweObject.serialize();
	}
	
	public static String decryptJwe(String jwe, String sharedSecret) throws Exception {
		EncryptedJWT encryptedJWT = EncryptedJWT.parse(jwe);
		encryptedJWT.decrypt(new AESDecrypter(sha256(sharedSecret)));
		return encryptedJWT.getPayload().toString();
	}
	
	
	public static String createJws(String jwe, String sharedSecret) throws Exception {
		Map<String, Object> customParameters = new HashMap<>();
		long iat = System.currentTimeMillis() / 1000;
		Long exp = iat + 120;
		customParameters.put("iat", iat);
		customParameters.put("exp", exp);
		
		//ADD CUSTOM
		customParameters.put("kid", apiKey);
		customParameters.put("typ", "JOSE");
		
		
		JWSHeader.Builder builder = new JWSHeader.Builder(JWSAlgorithm.HS256);
		builder.customParams(customParameters);
		JWSHeader jwsHeader = builder.build();
		JWSObject jwsObject = new JWSObject(jwsHeader, new Payload(jwe));
		jwsObject.sign(new MACSigner(sharedSecret.getBytes(StandardCharsets.UTF_8)));
		return jwsObject.serialize();
	}
	
	public static String verifyAndExtractJweFromJWS(String jws, String sharedSecret) throws Exception {
		JWSObject jwsObject = JWSObject.parse(jws);
		if (!jwsObject.verify(new MACVerifier(sharedSecret.getBytes(StandardCharsets.UTF_8)))) {
			throw new Exception("Invalid signature");
		}
		Map<String, Object> customParameters = jwsObject.getHeader().getCustomParams();
		if (customParameters == null) {
			throw new Exception("Invalid signature");
		}
		Long now = System.currentTimeMillis() / 1000;
		if ((Long) customParameters.get("iat") > now || (Long) customParameters.get("exp") < now) {
			throw new Exception("Invalid signature");
		}
		
		return jwsObject.getPayload().toString();
	}
	
	private static byte[] sha256(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(input.getBytes(StandardCharsets.UTF_8));
		return md.digest();
	}

}
