package pti.org.py.core;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PatientsResourceTest {

  private final String[] requiredAttributes = {
    "givenName", "familyName", "birthDate", "gender", "email"
  };

  public static JsonObjectBuilder getNewBuilder() {
    return Json.createObjectBuilder()
        .add("addressDescription", "A dos cuadras de la comisaria septima")
        .add("birthDate", "1992-04-08")
        .add("city", "Ciudad del Este")
        .add("email", "cecilia.casco7@gmail.com")
        .add("familyName", "Casco")
        .add("gender", "female")
        .add("givenName", "Cecilia")
        .add("id", "2")
        .add("identifier", "3915577")
        .add("job", "Software Engineer")
        .add("neighborhood", "San Jose")
        .add("phone", "0975773655")
        .add("photoUrl", "http://photourl/photo.jpeg")
        .add("street", "San Vicente");
  }

  @Test
  @Order(1)
  public void testGetPatients() {
    given().get("/patients").then().statusCode(200);
  }

  @Test
  @Order(2)
  public void testCreatePatient() {
    var json = getNewBuilder().build().toString();
    var request = given().body(json).header("Content-Type", "application/json").post("/patients");
    request.then().statusCode(201).header("Location", "/patients/2").body(is(json));
  }

  @Test
  @Order(3)
  public void testUpdatePatient() {
    var json = getNewBuilder().build().toString();
    var response = given().body(json).header("Content-Type", "application/json").put("/patients/2");
    response.then().statusCode(200).body(is(""));
  }

  @Test
  @Order(4)
  public void testGetPatient() {
    var json = getNewBuilder().build().toString();
    given().get("/patients/2").then().statusCode(200).body(is(json));
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
      request.body(json1).post("/patients").then().statusCode(400).body(containsString(response));
      request.body(json2).post("/patients").then().statusCode(400).body(containsString(response));
      request.body(json3).post("/patients").then().statusCode(400).body(containsString(response));
      request.body(json1).put("/patients/2").then().statusCode(400).body(containsString(response));
      request.body(json2).put("/patients/2").then().statusCode(400).body(containsString(response));
      request.body(json3).put("/patients/2").then().statusCode(400).body(containsString(response));
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
    request.body(json1).post("/patients").then().statusCode(400).body(containsString(response));
    request.body(json2).post("/patients").then().statusCode(400).body(containsString(response));
    request.body(json3).post("/patients").then().statusCode(400).body(containsString(response));
    request.body(json1).put("/patients/2").then().statusCode(400).body(containsString(response));
    request.body(json2).put("/patients/2").then().statusCode(400).body(containsString(response));
    request.body(json3).put("/patients/2").then().statusCode(400).body(containsString(response));
  }

  @Test
  @Order(7)
  public void testWrongEmailFormat() {
    var response = "{\"messages\":[";
    var json = getNewBuilder().add("email", "lsd;lj").build().toString();
    var request = given().header("Content-Type", "application/json");
    request.body(json).post("/patients").then().statusCode(400).body(containsString(response));
    request.body(json).put("/patients/2").then().statusCode(400).body(containsString(response));
  }

  @Test
  @Order(8)
  public void tetWrongGenderFormat() {
    var response = "{\"messages\":[";
    var json = getNewBuilder().add("gender", "lsd;lj").build().toString();
    var request = given().header("Content-Type", "application/json");
    request.body(json).post("/patients").then().statusCode(400).body(containsString(response));
    request.body(json).put("/patients/2").then().statusCode(400).body(containsString(response));
  }

  @Test
  @Order(9)
  public void tetValidGenderFormat() {
    var json1 = getNewBuilder().add("gender", "male").build().toString();
    var json3 = getNewBuilder().add("gender", "other").build().toString();
    var json4 = getNewBuilder().add("gender", "unknown").build().toString();
    var json2 = getNewBuilder().add("gender", "female").build().toString();
    var request = given().header("Content-Type", "application/json");
    request.body(json1).post("/patients").then().statusCode(201);
    request.body(json2).post("/patients").then().statusCode(201);
    request.body(json3).post("/patients").then().statusCode(201);
    request.body(json4).post("/patients").then().statusCode(201);
    request.body(json1).put("/patients/2").then().statusCode(200);
    request.body(json2).put("/patients/2").then().statusCode(200);
    request.body(json3).put("/patients/2").then().statusCode(200);
    request.body(json4).put("/patients/2").then().statusCode(200);
  }
}
