package Mersys;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import com.github.javafaker.Faker;

public class Fields {

    RequestSpecification reqSpec;
    Faker rndFaker = new Faker();
    String rndFieldName = "";
    String rndFieldCode = "";
    String fieldId="";

    @BeforeClass
    public void login() {

        baseURI = "https://test.mersys.io/";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", "turkeyts");
        userData.put("password", "TechnoStudy123");
        userData.put("rememberMe", "true");

        Cookies cookies =
                given()

                        .contentType(ContentType.JSON)
                        .body(userData)

                        .when()
                        .post("/auth/login")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        reqSpec = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void createNewField() {

        rndFieldName = rndFaker.name().firstName();
        rndFieldCode = rndFaker.code().imei();

        Map<String,String> newField=new HashMap<>();
        newField.put("name",rndFieldName);
        newField.put("code",rndFieldCode);
        newField.put("type","STRING");
        newField.put("schoolId","646cbb07acf2ee0d37c6d984");

        fieldId=
        given()
                .spec(reqSpec)
                .body(newField)

                .when()
                .post("school-service/api/entity-field")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id")
                ;
    }






}
