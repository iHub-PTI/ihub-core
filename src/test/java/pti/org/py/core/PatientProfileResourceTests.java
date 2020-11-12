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
public class PatientProfileResourceTests {

  private String getAccessToken(String user, String password) {
    var client = AuthzClient.create();
    var request = new AuthorizationRequest();
    var response = client.authorization(user, password).authorize(request);
    return response.getToken();
  }

  @Test
  @Order(1)
  void getPatientProfile() {
    var token = getAccessToken("3915577", "ceci");
    var json = PatientsResourceTest.getNewBuilder().build().toString();
    given().auth().oauth2(token).get("/profile/patient").then().statusCode(200).body(is(json));
  }

  @Test
  @Order(2)
  void createPatientProfile() {
    var token = getAccessToken("4690034", "test.1234");
    var response = given().auth().oauth2(token).get("/profile/patient");
    response.then().statusCode(200);
  }

  @Test
  @Order(3)
  void updatePatientProfile() {
    var token = getAccessToken("3915577", "ceci");
    var json = PatientsResourceTest.getNewBuilder().build().toString();
    var request = given().auth().oauth2(token).header("Content-Type", "application/json");
    request.body(json).put("/profile/patient").then().statusCode(200).body(is(""));
  }

  @Test
  @Order(4)
  public void testCreateAppointment() {
    var token = getAccessToken("3915577", "ceci");
    var appointment =
        AppointmentsResourceTest.getNewBuilder().remove("patientId").build().toString();
    var request = given().auth().oauth2(token).header("Content-Type", "application/json");
    request.body(appointment).post("/profile/patient/appointments").then().statusCode(201);
    request
        .get("/profile/patient/appointments")
        .then()
        .statusCode(200)
        .body(containsString("\"patientId\":\"2\""));
  }

  @Test
  @Order(5)
  public void testGetAppointment() {
    var token = getAccessToken("3915577", "ceci");
    var request = given().auth().oauth2(token);
    var response = request.get("/profile/patient/appointments/");
    var reader = Json.createReader(new StringReader(response.print()));
    var appointmentId = reader.readArray().get(0).asJsonObject().getString("id");
    var expected =
        AppointmentsResourceTest.getNewBuilder()
            .remove("doctor")
            .remove("patient")
            .build()
            .toString();
    request
        .get("/profile/patient/appointments/" + appointmentId)
        .then()
        .statusCode(200)
        .body(is(expected));
    reader.close();
  }

  @Test
  @Order(6)
  public void testNonExistentAppointment() {
    var token1 = getAccessToken("2480476", "rlopez");
    var response = given().auth().oauth2(token1).get("/profile/patient/appointments/");
    var reader = Json.createReader(new StringReader(response.print()));
    var appointmentId = reader.readArray().get(0).asJsonObject().getString("id");
    var token2 = getAccessToken("4690034", "test.1234");
    given()
        .auth()
        .oauth2(token2)
        .get("/profile/patient/appointments/" + appointmentId)
        .then()
        .statusCode(404);
    reader.close();
  }
}
