package py.org.pti.core.exception;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class FhirExceptionMapper implements ExceptionMapper<BaseServerResponseException> {

  @Inject Logger logger;

  @Override
  public Response toResponse(BaseServerResponseException e) {
    if (e instanceof ResourceGoneException) {
      return Response.status(Status.NOT_FOUND).build();
    }
    logger.log(Level.SEVERE, "FHIR Server Error", e);
    return Response.serverError().build();
  }
}
