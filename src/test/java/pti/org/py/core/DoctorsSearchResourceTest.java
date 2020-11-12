package pti.org.py.core;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import javax.json.Json;
import javax.json.JsonObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DoctorsSearchResourceTest {

  private JsonObject ruben =
      Json.createObjectBuilder()
          .add("addressDescription", "A dos cuadras de la comisaria septima")
          .add("biography", "Software Engineer")
          .add("birthDate", "1988-09-06")
          .add("city", "Asunción")
          .add("email", "rubenlop88@gmail.com")
          .add("familyName", "Lopez")
          .add("gender", "male")
          .add("givenName", "Ruben")
          .add("id", "1")
          .add("identifier", "2480476")
          .add("languages", Json.createArrayBuilder().add("gn").add("es"))
          .add("license", "123456789")
          .add("neighborhood", "Barrio Obrero")
          .add("phone", "0981372880")
          .add("specializations", Json.createArrayBuilder().add("1"))
          .add("street", "Misioneros Redentoristas")
          .build();

  private JsonObject ceci =
      Json.createObjectBuilder()
          .add("addressDescription", "A dos cuadras de la comisaria septima")
          .add("biography", "Software Engineer")
          .add("birthDate", "1988-09-06")
          .add("city", "Ciudad del Este")
          .add("email", "cecilia.casco7@gmail.com")
          .add("familyName", "Casco")
          .add("gender", "female")
          .add("givenName", "Cecilia")
          .add("id", "2")
          .add("identifier", "3915577")
          .add("languages", Json.createArrayBuilder().add("gn").add("es"))
          .add("license", "123456789")
          .add("neighborhood", "San Jose")
          .add("phone", "0975773655")
          .add("specializations", Json.createArrayBuilder().add("1"))
          .add("street", "Mariscal Lopez")
          .build();

  @Test
  @Order(2)
  public void testCreateDoctor() {
    var request = given().header("Content-Type", "application/json");
    request.body(ruben.toString()).post("/doctors");
    request.body(ceci.toString()).post("/doctors");
    given().get("/doctors?text=").then().statusCode(200);
    given().get("/doctors?text=Ruben").then().statusCode(200);
    given().get("/doctors?text=Ruben Ceci").then().statusCode(200);
    given().get("/doctors?text=Asu Ciudad").then().statusCode(200);
    given().get("/doctors?text=Misioneros").then().statusCode(200);
  }
}
