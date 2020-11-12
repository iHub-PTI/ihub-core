package pti.org.py.core;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import java.io.StringReader;
import javax.json.Json;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;

@QuarkusTest
public class DoctorProfileResourceTests {

  private String getAccessToken(String user, String password) {
    var client = AuthzClient.create();
    var request = new AuthorizationRequest();
    var response = client.authorization(user, password).authorize(request);
    return response.getToken();
  }

  @Test
  @Order(1)
  void getDoctorProfile() {
    var token = getAccessToken("2480476", "rlopez");
    var json = DoctorsResourceTest.getNewBuilder().build().toString();
    given().auth().oauth2(token).get("/profile/doctor").then().statusCode(200).body(is(json));
  }

  @Test
  @Order(2)
  void createDoctorProfile() {
    var token = getAccessToken("866025", "123");
    var response = given().auth().oauth2(token).get("/profile/doctor");
    response.then().statusCode(200);
  }

  @Test
  @Order(3)
  void updateDoctorProfile() {
    var token = getAccessToken("2480476", "rlopez");
    var json = DoctorsResourceTest.getNewBuilder().build().toString();
    var request = given().auth().oauth2(token).header("Content-Type", "application/json");
    request.body(json).put("/profile/doctor").then().statusCode(200).body(is(""));
  }

  @Test
  @Order(4)
  public void testCreateAppointment() {
    var token = getAccessToken("2480476", "rlopez");
    var appointment =
        AppointmentsResourceTest.getNewBuilder().remove("doctorId").build().toString();
    var request = given().auth().oauth2(token).header("Content-Type", "application/json");
    request.body(appointment).post("/profile/doctor/appointments").then().statusCode(201);
    request
        .get("/profile/doctor/appointments")
        .then()
        .statusCode(200)
        .body(containsString("\"doctorId\":\"1\""));
  }

  @Test
  @Order(5)
  public void testGetAppointment() {
    var token = getAccessToken("2480476", "rlopez");
    var request = given().auth().oauth2(token);
    var response = request.get("/profile/doctor/appointments/");
    var reader = Json.createReader(new StringReader(response.print()));
    var appointmentId = reader.readArray().get(0).asJsonObject().getString("id");
    var expected =
        AppointmentsResourceTest.getNewBuilder()
            .remove("doctor")
            .remove("patient")
            .build()
            .toString();
    request
        .get("/profile/doctor/appointments/" + appointmentId)
        .then()
        .statusCode(200)
        .body(is(expected));
    reader.close();
  }

  @Test
  @Order(6)
  public void testNonExistentAppointment() {
    var token1 = getAccessToken("2480476", "rlopez");
    var response = given().auth().oauth2(token1).get("/profile/doctor/appointments/");
    var reader = Json.createReader(new StringReader(response.print()));
    var appointmentId = reader.readArray().get(0).asJsonObject().getString("id");
    var token2 = getAccessToken("866025", "123");
    given()
        .auth()
        .oauth2(token2)
        .get("/profile/doctor/appointments/" + appointmentId)
        .then()
        .statusCode(404);
    reader.close();
  }
}
