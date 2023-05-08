//package com.example.kafka.producer.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.vault.authentication.ClientAuthentication;
//import org.springframework.vault.authentication.TokenAuthentication;
//import org.springframework.vault.client.VaultEndpoint;
//import org.springframework.vault.config.AbstractVaultConfiguration;
//
//@Configuration
//public class VaultConfig extends AbstractVaultConfiguration {
//
//	@Override
//	public VaultEndpoint vaultEndpoint() {
//		//return VaultEndpoint.create("localhost", 8200);
//		return new VaultEndpoint();
//	}
//
//	@Override
//	public ClientAuthentication clientAuthentication() {
//		return new TokenAuthentication("00000000-0000-0000-0000-000000000000");
//	}
//
//}
