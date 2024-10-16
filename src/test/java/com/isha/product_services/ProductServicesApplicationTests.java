package com.isha.product_services;

import io.restassured.RestAssured;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

import static org.hamcrest.Matchers.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServicesApplicationTests {
	@ServiceConnection
	static MongoDBContainer mongoDBContainer=new MongoDBContainer("mongo:7.0.5");
	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI="http://localhost";
		RestAssured.port=port;
    }

	static{
		mongoDBContainer.start();
		System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
	}
	@Test
	void shouldCreateProduct() {
		String requestBody= """
				{
				"name":"Iphone 15",
				"description":"Apple's latest iPhone",
                "price":12000.0
                }
				""";


		RestAssured.given()
				.contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/products")
                .then()
                .statusCode(201)
		        .body("id",notNullValue())
		        .body("name", equalTo("Iphone 15"))
		        .body("description", equalTo("Apple's latest iPhone"))
		        .body("price", equalTo(12000.0F));
	}
}
