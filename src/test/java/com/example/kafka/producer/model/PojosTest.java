package com.example.kafka.producer.model;

import static org.junit.Assert.assertTrue;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

import org.junit.jupiter.api.Test;

import pl.pojo.tester.api.assertion.Method;
public class PojosTest {
	
	@Test
	public void pojoAccountTest() {
	    
	    assertPojoMethodsFor(Account.class)
	    .testing(Method.GETTER, Method.SETTER, Method.TO_STRING)
        .testing(Method.EQUALS)
        .testing(Method.HASH_CODE)
        .testing(Method.CONSTRUCTOR)
        .areWellImplemented();
	}
	
	@Test
	public void pojoDeviceTest() {
	    assertPojoMethodsFor(Device.class)
	    .testing(Method.GETTER, Method.SETTER)
        .testing(Method.EQUALS)
        .testing(Method.HASH_CODE)
        .testing(Method.CONSTRUCTOR)
        .areWellImplemented();
	}
	
	@Test
	public void pojoEventoTest() {
	    assertPojoMethodsFor(Evento.class)
	    .testing(Method.GETTER, Method.SETTER)
        .areWellImplemented();
	}
	
	@Test
	public void pojoMasterCardDTOTest() {
	    assertPojoMethodsFor(MasterCardDTO.class)
	    .testing(Method.GETTER, Method.SETTER, Method.TO_STRING)
        .testing(Method.EQUALS)
        .testing(Method.HASH_CODE)
        .testing(Method.CONSTRUCTOR)
        .areWellImplemented();
	}
	
	@Test
	public void pojoMasterTokenTest() {
	    assertPojoMethodsFor(Token.class)
	    .testing(Method.GETTER, Method.SETTER)
        //.testing(Method.EQUALS)
        //.testing(Method.HASH_CODE)
        .areWellImplemented();
	    
	    Token token = new Token();
	    token.setAccountPanSequenceNumber("1234");
	    
	    assertTrue(token.toString() != null);
	    assertTrue(token.equals(token));
	    assertTrue(token.hashCode() != 0);	    
	}
}