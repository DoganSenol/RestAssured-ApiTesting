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

public class Discounts {


    Faker randomGenerator=new Faker();
    String DiscountId="";
    String DiscountDescription = "";
    String DiscountCode = "";
    boolean DiscountChecked = true;
    boolean DiscountUnChecked = false;
    int DiscountPriority;
    RequestSpecification reqSpec;

    @BeforeClass
    public void Login() {

        baseURI = "https://test.mersys.io/";


        Map<String, String> userLogin = new HashMap<>();
        userLogin.put("username", "turkeyts");
        userLogin.put("password", "TechnoStudy123");
        userLogin.put("rememberMe", "true");

        Cookies cookies =
                given()

                        .contentType(ContentType.JSON)
                        .body(userLogin)

                        .when()
                        .post("/auth/login")

                        .then()
                        //.log().all()
                        .log().body()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        reqSpec = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build();

    }

    @Test
    public void createNewDiscount() {

        DiscountDescription=randomGenerator.book().title();
        DiscountCode=randomGenerator.code().asin();
        DiscountPriority=randomGenerator.number().numberBetween(1, 3);

        Map<String,Object> newDiscount=new HashMap<>();
        newDiscount.put("description",DiscountDescription);
        newDiscount.put("code",DiscountCode);
        newDiscount.put("priority", DiscountPriority);
        newDiscount.put("active", String.valueOf(DiscountUnChecked));
        newDiscount.put("translateDescription",new Object[1]);



        DiscountId=
        given()
                .spec(reqSpec)
                .body(newDiscount)
                .log().body()

                .when()
                .post("/school-service/api/discounts")


                .then()
                .log().body()
                .statusCode(201)
                .extract().jsonPath().getString("id")
        ;
        System.out.println("DiscountId = " + DiscountId);

    }

    @Test(dependsOnMethods = "createNewDiscount")
    public void createNewDiscountNegative(){

        Map<String,Object> newDiscount=new HashMap<>();
        newDiscount.put("description",DiscountDescription);
        newDiscount.put("code",DiscountCode);
        newDiscount.put("priority", DiscountPriority);
        newDiscount.put("active", String.valueOf(DiscountUnChecked));
        newDiscount.put("translateDescription",new Object[1]);

        given()
                .spec(reqSpec)
                .body(newDiscount)
                .log().body()

                .when()
                .post("/school-service/api/discounts")


                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("already"))

        ;
    }

    @Test(dependsOnMethods = "createNewDiscount")
    public void updateTheDiscount(){

        DiscountDescription= randomGenerator.artist().name();
        Map<String,Object> updatedDiscount=new HashMap<>();
        updatedDiscount.put("id",DiscountId);
        updatedDiscount.put("description",DiscountDescription);
        updatedDiscount.put("code",DiscountCode);
        updatedDiscount.put("priority", DiscountPriority);
        updatedDiscount.put("active", String.valueOf(DiscountUnChecked));
        updatedDiscount.put("translateDescription",new Object[1]);



        given()
                .spec(reqSpec)
                .body(updatedDiscount)

                .when()
                .put("/school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(200)


        ;
    }
@Test(dependsOnMethods = "updateTheDiscount")
    public void deleteTheDiscount(){

        given()
                .spec(reqSpec)


                .when()
                .delete("/school-service/api/discounts/"+DiscountId)

                .then()
                .log().body()
                .statusCode(200)
                ;

    }

    @Test(dependsOnMethods ="deleteTheDiscount")
    public void deleteTheDiscountNegative(){
        given()
                .spec(reqSpec)

                .when()
                .delete("/school-service/api/discounts/"+DiscountId)


                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Discount not found"))



                ;



    }

}
