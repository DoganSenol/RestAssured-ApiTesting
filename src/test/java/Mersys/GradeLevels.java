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

public class GradeLevels {

    RequestSpecification reqSpec;
    Faker rndFaker = new Faker();
    String rndGrdLvlName = "";
    String rndGrdLvlShortName = "";
    String rndGrdLvlOrder = "";
    String grdLvlId = "";


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
    public void createNewGradeLevel() {

        rndGrdLvlName = rndFaker.name().lastName();
        rndGrdLvlShortName = rndFaker.name().username();
        rndGrdLvlOrder = rndFaker.code().ean8();

        Map<String, String> newGrdLvl = new HashMap<>();
        newGrdLvl.put("name", rndGrdLvlName);
        newGrdLvl.put("shortName", rndGrdLvlShortName);
        newGrdLvl.put("order", rndGrdLvlOrder);
        newGrdLvl.put("active", "true");
        newGrdLvl.put("enableForSelectedSchools", "true");

        grdLvlId=
                given()
                        .spec(reqSpec)
                        .body(newGrdLvl)

                        .when()
                        .post("school-service/api/grade-levels")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")
        ;
    }


    @Test(dependsOnMethods = "createNewGradeLevel")
    public void createNewGradeLevelNegative() {

        Map<String, String> newGrdLvl = new HashMap<>();
        newGrdLvl.put("name", rndGrdLvlName);
        newGrdLvl.put("shortName", rndGrdLvlShortName);
        newGrdLvl.put("order", rndGrdLvlOrder);
        newGrdLvl.put("active", "true");
        newGrdLvl.put("enableForSelectedSchools", "true");


        given()
                .spec(reqSpec)
                .body(newGrdLvl)

                .when()
                .post("school-service/api/grade-levels")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }




    @Test(dependsOnMethods = "createNewGradeLevel")
    public void updateGradeLevel() {
       String  newName=rndFaker.artist().name();

        Map<String, String> updatedGrdLvl = new HashMap<>();
        updatedGrdLvl.put("id",grdLvlId);
        updatedGrdLvl.put("name", newName);
        updatedGrdLvl.put("shortName", rndGrdLvlShortName);
        updatedGrdLvl.put("order", rndGrdLvlOrder);
        updatedGrdLvl.put("active", "true");
        updatedGrdLvl.put("enableForSelectedSchools", "true");

        given()
                .spec(reqSpec)
                .body(updatedGrdLvl)

                .when()
                .put("school-service/api/grade-levels")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", containsString(newName));
        ;
    }


    @Test(dependsOnMethods = "createNewGradeLevelNegative")
    public void deleteGradeLevel(){

        given()
                .spec(reqSpec)

                .when()
                .delete("school-service/api/grade-levels/"+grdLvlId)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteGradeLevel")
    public void deleteGradeLevelnegative(){

        given()
                .spec(reqSpec)

                .when()
                .delete("school-service/api/grade-levels/"+grdLvlId)

                .then()
                .log().body()
                .statusCode(400)
        ;
    }







}