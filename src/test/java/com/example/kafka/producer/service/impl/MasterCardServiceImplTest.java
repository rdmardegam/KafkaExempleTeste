package com.example.kafka.producer.service.impl;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.example.kafka.producer.service.CardService;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest

@RunWith(SpringRunner.class)
@WebMvcTest(CardService.class)
public class MasterCardServiceImplTest {

//	@Autowired
//	CardService cardService;

	@Autowired
	private CardService cardService;

	@MockBean
	private RestTemplate restTemplate;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		restTemplate = new RestTemplate();
	}

	@Test
	void Teste1() {

		// Gerando payload
		HttpEntity<String> entity = new HttpEntity<String>("{\r\n" + " \"SearchRequest\": {\r\n"
				+ "   \"AccountPan\": \"1234567890\",\r\n" + "   \"ExcludeDeletedIndicator\": \"false\",\r\n"
				+ "   \"AuditInfo\": {\r\n" + "      \"UserId\": \"A1435477\",\r\n"
				+ "      \"UserName\": \"John Smith\",\r\n" + "      \"Organization\": \"Any Bank\",\r\n"
				+ "      \"Phone\": \"5555551234\"\r\n" + "   }\r\n" + " }\r\n" + "}");

		// Efetua chamada
		// restTemplate.exchange("/search", HttpMethod.POST, entity, String.class);

		ResponseEntity<String> response = new ResponseEntity<String>("VALOR RESPONSE DE TESTE", HttpStatus.OK);
		Mockito.when(restTemplate.exchange("/search", HttpMethod.POST, entity, String.class)).thenReturn(response);
		// Mockito.doReturn(response).when(restTemplate).exchange("/search",
		// HttpMethod.POST, entity, String.class);

		cardService.ativarToken("1234567890", "1234567890");
	}

}
