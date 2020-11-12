package py.org.pti.core.exception;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

  @Override
  public Response toResponse(BadRequestException e) {
    var response = new ErrorResponse();
    response.messages.add(e.getMessage());
    return Response.status(Status.BAD_REQUEST).entity(response).build();
  }
}
