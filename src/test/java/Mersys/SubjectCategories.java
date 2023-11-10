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

public class SubjectCategories {
    Faker randomGenerator=new Faker();
    String  SubjectId="";
    String SubjectName ="";
    String SubjectCode ="";
    boolean SubjectChecked = true;
    boolean SubjectUnchecked= false;



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


        Object[] object=new Object[1];
        Map<String,Object> newSubject=new HashMap<>();
        newSubject.put("name",SubjectName);
        newSubject.put("code",SubjectCode);
        newSubject.put("active", String.valueOf(SubjectChecked));
        newSubject.put("translateName", new Object[1]);


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
    @Test(dependsOnMethods = "createNewSubjectCategories")
    public  void createNewSubjectCategoriesNegative(){

        Object[] object=new Object[1];
        Map<String,Object> newSubject=new HashMap<>();
        newSubject.put("name",SubjectName);
        newSubject.put("code",SubjectCode);
        newSubject.put("active",SubjectChecked);
        newSubject.put("translateName",new Object[1]);

        given()
                .spec(reqSpec)
                .body(newSubject)
                .log().body()


                .when()
                .post("/school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("already"))


                ;
    }

    @Test(dependsOnMethods = "createNewSubjectCategoriesNegative" )
    public void updateSubjectCategories(){
        SubjectCode=randomGenerator.code().ean8();
        String newSubjectName=randomGenerator.chuckNorris().fact();
        Map<String,String> updatedSubjects=new HashMap<>();
        updatedSubjects.put("id",SubjectId);
        updatedSubjects.put("name",newSubjectName);
        updatedSubjects.put("code",SubjectCode);


        given()
                .spec(reqSpec)
                .body(updatedSubjects)



                .when()
                .put("/school-service/api/subject-categories")


                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(newSubjectName))

        ;

    }
    @Test(dependsOnMethods = "updateSubjectCategories")
    public void deleteSubjectCategories(){

        given()
                .spec(reqSpec)

                .when()
                .delete("/school-service/api/subject-categories/"+SubjectId)

                .then()
                .log().body()
                .statusCode(200)


                ;


    }



}
