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

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.BeforeClass;

public class SchoolLocations {

    Faker faker=new Faker();
    String SchoolID="";
    String Name="";
    String ShortName="";
    boolean classActive=true;
    boolean classNotActive=false;
    int Capacity;
    String Type="";
    String schoolNumber="646cbb07acf2ee0d37c6d984";
    RequestSpecification requestSpecification;




    @BeforeClass
    public  void Login() {

        baseURI= "https://test.mersys.io/";

        Map<String,String> userlogin=new HashMap<>();
        userlogin.put("username","turkeyts");
        userlogin.put("password","TechnoStudy123");
        userlogin.put("rememberMe", "true");
        userlogin.put("school","1238463");
        userlogin.put("active", String.valueOf(classNotActive));

        Cookies cookies=
        given()
                .contentType(ContentType.JSON)
                .body(userlogin)

                .when()
                .post("/auth/login")


                .then()
                .log().body()
                .statusCode(200)
                .extract().response().getDetailedCookies()

                ;
        requestSpecification= new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build();

    }

    @Test
    public void createNewSchoolLocation(){

        Name=faker.funnyName().name();
        ShortName=faker.book().title();

        Map<String,String > newSchoolLocation=new HashMap<>();
        newSchoolLocation.put("name", Name);
        newSchoolLocation.put("shortName",ShortName);
        newSchoolLocation.put("capacity", String.valueOf(7));
        newSchoolLocation.put("type","LABORATORY");
        newSchoolLocation.put("school",schoolNumber);
        newSchoolLocation.put("active", String.valueOf(classNotActive));

        SchoolID=
        given()
                .spec(requestSpecification)
                .body(newSchoolLocation)
                .log().body()

                .when()
                .post("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id")
        ;
        System.out.println("SchoolID = " + SchoolID);

    }

    @Test(dependsOnMethods = "createNewSchoolLocation")
    public void createNewSchoolLocationNegative(){


        Map<String,String > newSchoolLocation=new HashMap<>();
        newSchoolLocation.put("name", Name);
        newSchoolLocation.put("shortName",ShortName);
        newSchoolLocation.put("capacity", String.valueOf(7));
        newSchoolLocation.put("type","LABORATORY");
        newSchoolLocation.put("school",schoolNumber);
        newSchoolLocation.put("active", String.valueOf(classNotActive));


                given()
                        .spec(requestSpecification)
                        .body(newSchoolLocation)
                        .log().body()

                        .when()
                        .post("/school-service/api/location")

                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message",containsString("already exists"))

        ;
    }

    @Test(dependsOnMethods = "createNewSchoolLocation")
    public void editTheSchoolLocation(){

        Map<String,String> editSchoolLocation=new HashMap<>();
        editSchoolLocation.put("id",SchoolID);
        editSchoolLocation.put("name", "IAmQaEngineer4");
        editSchoolLocation.put("shortName",ShortName);
        editSchoolLocation.put("capacity", String.valueOf(7));
        editSchoolLocation.put("type","LABORATORY");
        editSchoolLocation.put("school",schoolNumber);
        editSchoolLocation.put("active", String.valueOf(classNotActive));

        given()
                .spec(requestSpecification)
                .body(editSchoolLocation)

                .when()
                .put("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(200)

        ;
    }

    @Test(dependsOnMethods = "editTheSchoolLocation")
    public void deleteTheSchoolLocation(){

        given()
                .spec(requestSpecification)

                .when()
                .delete("/school-service/api/location/"+SchoolID)

                .then()
                .log().body()
                .statusCode(200)

        ;

    }
    @Test(dependsOnMethods = "deleteTheSchoolLocation")
    public void deleteTheSchoolLocationNegativeTest(){

        given()
                .spec(requestSpecification)

                .when()
                .delete("/school-service/api/location/"+SchoolID)

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("School Location not found"))

        ;

    }
}
