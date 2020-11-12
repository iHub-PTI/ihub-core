package pti.org.py.core;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class SpecialitiesResourceTest {

  @Test
  public void testGetSpecialities() {
    var responseBody =
        "[{\"description\":\"ALERGIA E INMUNOLOGIA\",\"id\":\"1\"},"
            + "{\"description\":\"ALERGIA E INMUNOLOGIA CLINICA\",\"id\":\"2\"},"
            + "{\"description\":\"ALERGIA EN PEDIATRIA\",\"id\":\"3\"},"
            + "{\"description\":\"ANGIOLOGIA Y CIRUGIA VASCULAR\",\"id\":\"4\"},"
            + "{\"description\":\"CIRUGIA DE CABEZA Y CUELLO\",\"id\":\"5\"},"
            + "{\"description\":\"CIRUGIA GENERAL - NO USAR\",\"id\":\"6\"},"
            + "{\"description\":\"CIRUGIA PLASTICA Y RECONSTRUCTIVA\",\"id\":\"7\"},"
            + "{\"description\":\"CIRUGIA RECONSTRUCTIVA Y QUEMADURAS\",\"id\":\"8\"},"
            + "{\"description\":\"CIRUGIA TORACICA\",\"id\":\"9\"},"
            + "{\"description\":\"CIRUGIA VASCULAR\",\"id\":\"10\"}]";
    given().get("/specializations").then().statusCode(200).body(is(responseBody));
  }
}
