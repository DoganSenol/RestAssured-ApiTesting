package Mersys;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class PositionCatagories {

 RequestSpecification reqSpec;
public Faker random = new Faker();

@BeforeClass
public void Login(){

        baseURI= "https://test.mersys.io/";


        Map<String, String> userLogin=new HashMap<>();
        userLogin.put("username","turkeyts");
        userLogin.put("password", "TechnoStudy123");
        userLogin.put("rememberMe", "true");

        Cookies cookies=
        given()

        .contentType(ContentType.JSON)
        .body(userLogin)

        .when()
        .post("/auth/login")

        .then()
        //.log().all()
        .log().body()
        .statusCode(200)
        .extract().response().getDetailedCookies()


        ;

        reqSpec= new RequestSpecBuilder()
        .addCookies(cookies)
        .setContentType(ContentType.JSON)
        .build();

        }
        String positionID = "";
private String positionName = "";
@Test
public void createPosition() {
        String rndPositionName = random.name().firstName();
        positionName = rndPositionName;

        Map<String, String> newPosition = new HashMap<>();
        newPosition.put("name", rndPositionName);

        positionID =
        given()
        .spec(reqSpec)
        .body(newPosition)
        .when()
        .post("school-service/api/position-category")
        .then()
        .contentType(ContentType.JSON)
        .statusCode(201)
        .extract().path("id");
        }
        @Test(dependsOnMethods = "createPosition")
        public  void createPositionNegative(){
        Map<String,String> newPosition = new HashMap<>();
        newPosition.put("name",positionName);

        given()
                .spec(reqSpec)
                .body(newPosition)
                .when()
                .post("school-service/api/position-category")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(400);

        }
        @Test(dependsOnMethods = "createPositionNegative")
        public  void updatePosition(){
        Map<String,String> updatePostion= new HashMap<>();
        updatePostion.put("id",positionID);
        updatePostion.put("name","Jony");

        given()
                .spec(reqSpec)
                .body(updatePostion)
                .when()
                .post("school-service/api/position-category")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("name",equalTo("Jony"));

        }





























}
