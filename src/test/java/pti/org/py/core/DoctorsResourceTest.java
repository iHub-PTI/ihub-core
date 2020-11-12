package pti.org.py.core;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DoctorsResourceTest {

  private final String[] requiredAttributes = {
    "givenName", "familyName", "languages", "birthDate", "gender", "email", "specializations",
  };

  public static JsonObjectBuilder getNewBuilder() {
    return Json.createObjectBuilder()
        .add("addressDescription", "A dos cuadras de la comisaria septima")
        .add("biography", "Software Engineer")
        .add("birthDate", "1988-09-06")
        .add("city", "Ciudad del Este")
        .add("email", "rubenlop88@gmail.com")
        .add("familyName", "Lopez")
        .add("gender", "male")
        .add("givenName", "Ruben")
        .add("id", "1")
        .add("identifier", "2480476")
        .add("languages", Json.createArrayBuilder().add("gn").add("es"))
        .add("license", "123456789")
        .add("neighborhood", "San Jose")
        .add("phone", "0975773655")
        .add("photoUrl", "http://photourl/photo.jpeg")
        .add("specializations", Json.createArrayBuilder().add("1"))
        .add("street", "Mariscal Lopez");
  }

  @Test
  @Order(1)
  public void testGetDoctors() {
    given().get("/doctors").then().statusCode(200);
  }

  @Test
  @Order(2)
  public void testCreateDoctor() {
    var json = getNewBuilder().build().toString();
    var response = given().body(json).header("Content-Type", "application/json").post("/doctors");
    response.then().statusCode(201).header("Location", "/doctors/1");
  }

  @Test
  @Order(3)
  public void testUpdateDoctor() {
    var json = getNewBuilder().build().toString();
    var response = given().body(json).header("Content-Type", "application/json").put("/doctors/1");
    response.then().statusCode(200).body(is(""));
  }

  @Test
  @Order(4)
  public void testGetDoctor() {
    var json =
        getNewBuilder()
            .remove("birthDate")
            .remove("gender")
            .remove("email")
            .remove("phone")
            .build()
            .toString();
    given().get("/doctors/1").then().statusCode(200).body(is(json));
  }

  @Test
  @Order(5)
  public void testRequiredAttributes() {
    for (var attribute : requiredAttributes) {
      var response = "{\"messages\":[";
      var json1 = getNewBuilder().remove(attribute).build().toString();
      var json2 = getNewBuilder().addNull(attribute).build().toString();
      var json3 = getNewBuilder().add(attribute, "").build().toString();
      var request = given().header("Content-Type", "application/json");
      request.body(json1).post("/doctors").then().statusCode(400).body(containsString(response));
      request.body(json1).put("/doctors/1").then().statusCode(400).body(containsString(response));
      request.body(json2).post("/doctors").then().statusCode(400).body(containsString(response));
      request.body(json2).put("/doctors/1").then().statusCode(400).body(containsString(response));
      request.body(json3).post("/doctors").then().statusCode(400).body(containsString(response));
      request.body(json3).put("/doctors/1").then().statusCode(400).body(containsString(response));
    }
  }

  @Test
  @Order(5)
  public void testNotEmptyArrays() {
    for (var attribute : List.of("languages", "specializations")) {
      var response = "{\"messages\":[";
      var json = getNewBuilder().add(attribute, Json.createArrayBuilder()).build().toString();
      var request = given().header("Content-Type", "application/json");
      request.body(json).post("/doctors").then().statusCode(400).body(containsString(response));
      request.body(json).put("/doctors/1").then().statusCode(400).body(containsString(response));
    }
  }

  @Test
  @Order(6)
  public void testWrongDateFormat() {
    var response = "{\"messages\":[";
    var json1 = getNewBuilder().add("birthDate", "lsd;lj").build().toString();
    var json2 = getNewBuilder().add("birthDate", "2020-02-30").build().toString();
    var json3 = getNewBuilder().add("birthDate", "2020-02-01'T'04:20:00").build().toString();
    var request = given().header("Content-Type", "application/json");
    request.body(json1).post("/doctors").then().statusCode(400).body(containsString(response));
    request.body(json2).post("/doctors").then().statusCode(400).body(containsString(response));
    request.body(json3).post("/doctors").then().statusCode(400).body(containsString(response));
    request.body(json1).put("/doctors/1").then().statusCode(400).body(containsString(response));
    request.body(json2).put("/doctors/1").then().statusCode(400).body(containsString(response));
    request.body(json3).put("/doctors/1").then().statusCode(400).body(containsString(response));
  }

  @Test
  @Order(7)
  public void testWrongEmailFormat() {
    var response = "{\"messages\":[";
    var json = getNewBuilder().add("email", "lsd;lj").build().toString();
    var request = given().header("Content-Type", "application/json");
    request.body(json).post("/doctors").then().statusCode(400).body(containsString(response));
    request.body(json).put("/doctors/1").then().statusCode(400).body(containsString(response));
  }

  @Test
  @Order(8)
  public void tetWrongGenderFormat() {
    var response = "{\"messages\":[";
    var json = getNewBuilder().add("gender", "lsd;lj").build().toString();
    var request = given().header("Content-Type", "application/json");
    request.body(json).post("/doctors").then().statusCode(400).body(containsString(response));
    request.body(json).put("/doctors/1").then().statusCode(400).body(containsString(response));
  }

  @Test
  @Order(9)
  public void tetValidGenderFormat() {
    var json1 = getNewBuilder().add("gender", "unknown").build().toString();
    var json2 = getNewBuilder().add("gender", "female").build().toString();
    var json3 = getNewBuilder().add("gender", "other").build().toString();
    var json4 = getNewBuilder().add("gender", "male").build().toString();
    var request = given().header("Content-Type", "application/json");
    request.body(json1).post("/doctors").then().statusCode(201);
    request.body(json2).post("/doctors").then().statusCode(201);
    request.body(json3).post("/doctors").then().statusCode(201);
    request.body(json4).post("/doctors").then().statusCode(201);
    request.body(json1).put("/doctors/1").then().statusCode(200);
    request.body(json2).put("/doctors/1").then().statusCode(200);
    request.body(json3).put("/doctors/1").then().statusCode(200);
    request.body(json4).put("/doctors/1").then().statusCode(200);
  }

  @Test
  @Order(10)
  public void testInvalidSpecialtyId() {
    var response = "{\"messages\":[";
    var json1 =
        getNewBuilder()
            .add("specializations", Json.createArrayBuilder().add("lsd;lj"))
            .build()
            .toString();
    var json2 =
        getNewBuilder()
            .add("specializations", Json.createArrayBuilder().add("50"))
            .build()
            .toString();
    var request = given().header("Content-Type", "application/json");
    request.body(json1).post("/doctors").then().statusCode(400).body(containsString(response));
    request.body(json2).post("/doctors").then().statusCode(400).body(containsString(response));
    request.body(json1).put("/doctors/1").then().statusCode(400).body(containsString(response));
    request.body(json2).put("/doctors/1").then().statusCode(400).body(containsString(response));
  }
}
