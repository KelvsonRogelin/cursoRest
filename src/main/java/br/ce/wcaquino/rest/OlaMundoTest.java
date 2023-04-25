package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundoTest {

	@Test
	public void testOlaMundo() {
		Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
		Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		Assert.assertTrue(response.statusCode() == 200);
		Assert.assertTrue("Status code deveria ser 200", response.statusCode() == 200);
		Assert.assertEquals(200, response.statusCode());
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	}

	@Test
	public void devoConhecerOutrasFormasRestAssured() {
		Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);

		get("http://restapi.wcaquino.me/ola").then().statusCode(200);

		given() 			//Pré condições
			.when()			//Metodos hhtp get,post etc.
				.get("http://restapi.wcaquino.me/ola")
			.then()			//verificações
				.statusCode(200);
	}
	
	@Test
	public void devoConhecerMatchersHamcrest() {
		assertThat("Maria", Matchers.is("Maria")); // comparar dado
		assertThat(128, Matchers.isA(Integer.class));
		assertThat(128d, Matchers.isA(Double.class)); // valitar tipo de dado inteiro double string
		assertThat(128d, Matchers.greaterThan(120d)); //Maior que
		assertThat(128d, Matchers.lessThan(130d)); //Menor que
		
		List<Integer> impares = Arrays.asList(1,3,5,7,9);
		assertThat(impares, hasSize(5)); // Validar tamanha da lista
		assertThat(impares, contains(1,3,5,7,9)); // Contem na mesma ordem
		assertThat(impares, containsInAnyOrder(1,3,7,9,5));// Contem em qualquer ordem
		assertThat(impares, hasItem(1));//verificar 1 item da lista
		assertThat(impares, hasItems(1,5));//verificar maisde um item da lista
		
		assertThat("Maria", is(not("João"))); // não é 
		assertThat("Maria", not("João")); // não é
		assertThat("Joaquina", anyOf(is("Maria"), is("Joaquina"))); // um ou outro
		assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui")));// começa com, termina com, e contem
	}

}
