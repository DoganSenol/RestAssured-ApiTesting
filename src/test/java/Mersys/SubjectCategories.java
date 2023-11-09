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


public class SubjectCategories {
    Faker randomGenerator=new Faker();
    String  SubjectId="";
    String SubjectName ="";
    String SubjectCode ="";
    boolean SubjectChecked = true;



    RequestSpecification reqSpec;

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
    @Test
    public void createNewSubjectCategories(){

        SubjectName=randomGenerator.name().fullName();
        SubjectCode=randomGenerator.code().asin();


        Map<String,String> newSubject=new HashMap<>();
        newSubject.put("name",SubjectName);
        newSubject.put("code",SubjectCode);
        newSubject.put("active", String.valueOf(SubjectChecked));


        SubjectId=
        given()
                .spec(reqSpec)
                .body(newSubject)
                .log().body()

                .when()
                .post("/school-service/api/subject-categories")


                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id")


                ;
        System.out.println("newSubject = " + newSubject);




    }


}
