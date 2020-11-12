package pti.org.py.core;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AppointmentsResourceTest {

  private final String[] requiredAttributes = {
    "doctorId", "end", "patientId", "start",
  };

  public static JsonObjectBuilder getNewBuilder() {
    return Json.createObjectBuilder()
        .add("description", "Reunion de relevamiento")
        .addNull("doctor")
        .add("doctorId", "1")
        .add("end", "2020-01-01T17:30:00.000-0300")
        .add("id", "3")
        .addNull("patient")
        .add("patientId", "2")
        .add("start", "2020-01-01T17:00:00.000-0300");
  }

  public static JsonObjectBuilder getNewDoctorBuilder() {
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
        .add("specializations", Json.createArrayBuilder().add("1"))
        .add("street", "Mariscal Lopez");
  }

  public static JsonObjectBuilder getNewPatientBuilder() {
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
        .add("street", "San Vicente");
  }

  @Test
  @Order(1)
  public void testCreateAppointment() {
    var doctor = getNewDoctorBuilder().build().toString();
    var patient = getNewPatientBuilder().build().toString();
    var appointment = getNewBuilder().build().toString();
    var request = given().header("Content-Type", "application/json");
    request.body(doctor).post("/doctors").then().header("Location", "/doctors/1");
    request.body(patient).post("/patients").then().header("Location", "/patients/2");
    request.body(appointment).post("/appointments").then().statusCode(201);
    request.get("/appointments").then().statusCode(200);
  }

  @Test
  @Order(2)
  public void getAppointment() {
    var response = given().get("/appointments");
    var reader = Json.createReader(new StringReader(response.print()));
    var appointmentId = reader.readArray().get(0).asJsonObject().getString("id");
    var expected = getNewBuilder().remove("doctor").remove("patient").build().toString();
    given().get("/appointments/" + appointmentId).then().statusCode(200).body(is(expected));
    reader.close();
  }

  @Test
  @Order(3)
  public void getAppointmentIncludeDoctor() {
    var response = given().get("/appointments");
    var reader = Json.createReader(new StringReader(response.print()));
    var appointmentId = reader.readArray().get(0).asJsonObject().getString("id");
    var doctor =
        getNewDoctorBuilder().remove("birthDate").remove("email").remove("gender").remove("phone");
    var expected = getNewBuilder().add("doctor", doctor).remove("patient").build().toString();
    given()
        .get("/appointments/" + appointmentId + "?include=doctor")
        .then()
        .statusCode(200)
        .body(is(expected));
    reader.close();
  }

  @Test
  @Order(4)
  public void getAppointmentIncludePatient() {
    var response = given().get("/appointments");
    var reader = Json.createReader(new StringReader(response.print()));
    var appointmentId = reader.readArray().get(0).asJsonObject().getString("id");
    var expected =
        getNewBuilder().remove("doctor").add("patient", getNewPatientBuilder()).build().toString();
    given()
        .get("/appointments/" + appointmentId + "?include=patient")
        .then()
        .statusCode(200)
        .body(is(expected));
    reader.close();
  }

  @Test
  @Order(5)
  public void getAppointmentsForDoctor() {
    var response = given().get("/appointments?doctors=1");
    var reader = Json.createReader(new StringReader(response.print()));
    var expected = getNewBuilder().remove("doctor").remove("patient").build().toString();
    assertEquals(expected, reader.readArray().get(0).toString());
    reader.close();
  }

  @Test
  @Order(6)
  public void getAppointmentsForPatients() {
    var response = given().get("/appointments?patients=2");
    var reader = Json.createReader(new StringReader(response.print()));
    var expected = getNewBuilder().remove("doctor").remove("patient").build().toString();
    assertEquals(expected, reader.readArray().get(0).toString());
    reader.close();
  }

  @Test
  @Order(7)
  public void getAppointmentsForDoctorAndPatient() {
    var response = given().get("/appointments?doctors=1&patients=2");
    var reader = Json.createReader(new StringReader(response.print()));
    var expected = getNewBuilder().remove("doctor").remove("patient").build().toString();
    assertEquals(expected, reader.readArray().get(0).toString());
    reader.close();
  }

  @Test
  @Order(8)
  public void getAppointmentsForNonExistentDoctor() {
    var response = given().get("/appointments?doctors=2");
    var reader = Json.createReader(new StringReader(response.print()));
    assertTrue(reader.readArray().isEmpty());
    reader.close();
  }

  @Test
  @Order(9)
  public void getAppointmentsForNonExistentPatients() {
    var response = given().get("/appointments?patients=1");
    var reader = Json.createReader(new StringReader(response.print()));
    assertTrue(reader.readArray().isEmpty());
    reader.close();
  }

  @Test
  @Order(10)
  public void getAppointmentsIncludeDoctor() {
    var response = given().get("/appointments?include=doctor");
    var reader = Json.createReader(new StringReader(response.print()));
    var doctor =
        getNewDoctorBuilder().remove("birthDate").remove("email").remove("gender").remove("phone");
    var expected = getNewBuilder().add("doctor", doctor).remove("patient").build().toString();
    assertEquals(expected, reader.readArray().get(0).toString());
    reader.close();
  }

  @Test
  @Order(11)
  public void getAppointmentsIncludePatient() {
    var response = given().get("/appointments?include=patient");
    var reader = Json.createReader(new StringReader(response.print()));
    var expected =
        getNewBuilder().add("patient", getNewPatientBuilder()).remove("doctor").build().toString();
    assertEquals(expected, reader.readArray().get(0).toString());
    reader.close();
  }

  @Test
  @Order(12)
  public void getAppointmentsIncludeDoctorAndPatient() {
    var response = given().get("/appointments?include=doctor,patient");
    var reader = Json.createReader(new StringReader(response.print()));
    var doctor =
        getNewDoctorBuilder().remove("birthDate").remove("email").remove("gender").remove("phone");
    var expected =
        getNewBuilder()
            .add("doctor", doctor)
            .add("patient", getNewPatientBuilder())
            .build()
            .toString();
    assertEquals(expected, reader.readArray().get(0).toString());
    reader.close();
  }

  @Test
  @Order(13)
  public void getAppointmentsWrongInclude() {
    var response = given().get("/appointments?include=asdoctorasd,patientas");
    var reader = Json.createReader(new StringReader(response.print()));
    var expected = getNewBuilder().remove("patient").remove("doctor").build().toString();
    assertEquals(expected, reader.readArray().get(0).toString());
    reader.close();
  }

  @Test
  @Order(14)
  public void testRequiredAttributes() {
    for (var attribute : requiredAttributes) {
      var response = "{\"messages\":[";
      var json1 = getNewBuilder().remove(attribute).build().toString();
      var json2 = getNewBuilder().addNull(attribute).build().toString();
      var json3 = getNewBuilder().add(attribute, "").build().toString();
      var request = given().header("Content-Type", "application/json");
      request
          .body(json1)
          .post("/appointments")
          .then()
          .statusCode(400)
          .body(containsString(response));
      request
          .body(json2)
          .post("/appointments")
          .then()
          .statusCode(400)
          .body(containsString(response));
      request
          .body(json3)
          .post("/appointments")
          .then()
          .statusCode(400)
          .body(containsString(response));
    }
  }

  @Test
  @Order(15)
  public void testWrongDateFormat() {
    var response = "{\"messages\":[";
    var json1 = getNewBuilder().add("start", "lsd;lj").build().toString();
    var json2 = getNewBuilder().add("start", "2020-02-30").build().toString();
    var json3 = getNewBuilder().add("start", "2020-02-01'T'04:20:0").build().toString();
    var request = given().header("Content-Type", "application/json");
    request.body(json1).post("/appointments").then().statusCode(400).body(containsString(response));
    request.body(json2).post("/appointments").then().statusCode(400).body(containsString(response));
    request.body(json3).post("/appointments").then().statusCode(400).body(containsString(response));
  }

  @Test
  @Order(16)
  public void testNonExistentDoctorAndPatient() {
    var response = "{\"messages\":[";
    var json1 = getNewBuilder().add("doctorId", "10").build().toString();
    var json2 = getNewBuilder().add("patientId", "10").build().toString();
    var request = given().header("Content-Type", "application/json");
    request.body(json1).post("/appointments").then().statusCode(400).body(containsString(response));
    request.body(json2).post("/appointments").then().statusCode(400).body(containsString(response));
  }
}
