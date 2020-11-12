package py.org.pti.core.exception;

import javax.json.bind.JsonbException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonbExceptionMapper implements ExceptionMapper<ProcessingException> {

  @Override
  public Response toResponse(ProcessingException e) {
    var response = new ErrorResponse();
    if (e.getCause() instanceof JsonbException) {
      response.messages.add(e.getCause().getMessage());
    } else {
      response.messages.add(e.getMessage()); // We don't have a test case for this.
    }
    return Response.status(Status.BAD_REQUEST).entity(response).build();
  }
}
