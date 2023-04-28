package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserJsonTest {
	@Test
	public void deveVerificarPrimeiroNivel() {
		given() 		
		.when()			
			.get("http://restapi.wcaquino.me/users/1")
		.then()	
			.statusCode(200)
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18));
		}
	@Test
	public void deveVerificarPrimeiroNivelOutrasFormas() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/users/1");
		//path
		Assert.assertEquals(new Integer(1), response.path("id"));
		Assert.assertEquals(new Integer(1), response.path("%s","id"));
		
		//json path
		JsonPath jpath = new JsonPath(response.asString());
		Assert.assertEquals(1, jpath.getInt("id"));
		
		//from
		int id = JsonPath.from(response.asString()).getInt("id");
		Assert.assertEquals(1, id);
		
		}
	
	@Test
	public void deveVerificarSegundoNivel() {
		given() 		
		.when()			
			.get("http://restapi.wcaquino.me/users/2")
		.then()	
			.statusCode(200)
			.body("id", is(2))
			.body("name", containsString("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos"));
		}

	@Test
	public void deveVerificarLista() {
		given() 		
		.when()			
			.get("http://restapi.wcaquino.me/users/3")
		.then()	
			.statusCode(200)
			.body("id", is(3))
			.body("name", containsString("Ana"))
			.body("filhos", hasSize(2)) //Tamanho da lista
			.body("filhos[0].name", is("Zezinho")) // verifica o primeiro item da lista
			.body("filhos[1].name", is("Luizinho"))// verifica segundo item da lista
			.body("filhos.name", hasItem("Zezinho"))
			.body("filhos.name", hasItems("Zezinho","Luizinho"));
		}
	
	@Test
	public void deveRetornarErroUsuarioInexistente() {
		given() 		
		.when()			
			.get("http://restapi.wcaquino.me/users/4")
		.then()	
			.statusCode(404)
			.body("error",is("Usuário inexistente"))
			;
		}
	
	@Test
	public void deveVerificarListaRaiz() {
		given() 		
		.when()			
			.get("http://restapi.wcaquino.me/users")
		.then()	
			.statusCode(200)
			.body("$", hasSize(3))// busca na raiz $, e tamanho do json, no caso 3 objetos
			.body("name", hasItems("João da Silva","Maria Joaquina", "Ana Júlia"))// pesquisa retorna todos os nome do json
			.body("age[1]", is(25)) // aqui pega a idade do segundo objeto do json 
			.body("filhos.name", hasItem(Arrays.asList("Zezinho","Luizinho")))
			.body("salary", contains(1234.5677f, 2500, null))
			;
		}
	
	@Test
	public void devoFazerVerificacoesAvancadas() {
		given() 		
		.when()			
			.get("http://restapi.wcaquino.me/users")
		.then()	
			.statusCode(200)
			.body("$", hasSize(3))
			.body("age.findAll{it <=25}.size()", is(2)) 
			//findAll {it} serve para fazer uma pesquisa dentro do json no caso todos ate 25 anos
			.body("age.findAll{it <=25 && it >20}.size()", is(1))// aqui é o resultado menos que 25 e maio que 20
			.body("findAll{it.age <=25 && it.age >20}.name", hasItem("Maria Joaquina"))
			//ai o findAll procura entre os objetos do json, o it se referencia ao objeto
			.body("findAll{it.age <=25}[0].name", is("Maria Joaquina"))//nome do primeiro registro
			.body("find{it.age <=25}.name", is("Maria Joaquina"))
			.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina","Ana Júlia"))
			.body("findAll{it.name.length() > 10}.name", hasItems("João da Silva","Maria Joaquina"))
			.body("name.collect{it.toUpperCase()}",hasItem("MARIA JOAQUINA"))// transforma um nome para todo maisculo
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}",hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()",allOf(arrayContaining("MARIA JOAQUINA"),arrayWithSize(1)))
			.body("age.collect{it * 2}", hasItems(60,50,40))
			.body("id.max()", is(3))
			.body("salary.min()", is(1234.5678f))
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
			.body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))
			;
		}
	
	@Test
	public void devoUnirJsonPathComJAVA() {
		ArrayList<String> names =
		given() 		
		.when()			
			.get("http://restapi.wcaquino.me/users")
		.then()	
			.statusCode(200)
			.extract().path("name.findAll{it.startsWith('Maria')}")
			;
		Assert.assertEquals(1, names.size());
		Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa Joaquina"));
		Assert.assertEquals(names.get(0).toUpperCase(),"maria joaquina".toUpperCase());
		}
}
