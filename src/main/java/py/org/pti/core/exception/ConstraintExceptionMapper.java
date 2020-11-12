package py.org.pti.core.exception;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConstraintExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

  @Override
  public Response toResponse(ConstraintViolationException e) {
    var response = new ErrorResponse();
    for (var violation : e.getConstraintViolations()) {
      response.messages.add(violation.getMessage());
    }
    return Response.status(Status.BAD_REQUEST).entity(response).build();
  }
}
