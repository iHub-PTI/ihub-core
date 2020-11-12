package py.org.pti.core.resource;

import static java.util.stream.Collectors.toList;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import py.org.pti.core.dto.DoctorDto;
import py.org.pti.core.dto.DoctorPublicDto;
import py.org.pti.core.service.DoctorsService;

@Path("/doctors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DoctorsResource {

  @Inject DoctorsService service;

  @Inject Validator validator;

  @GET
  public Response list(@QueryParam("text") String text) {
    var list = service.list(text).stream().map(DoctorPublicDto::new).collect(toList());
    return Response.ok(list).build();
  }

  @POST
  public Response create(@Context UriInfo uriInfo, DoctorDto body) {
    checkViolations(body);
    var id = service.create(body);
    var uri = UriBuilder.fromPath(uriInfo.getPath()).path(id).build();
    return Response.status(Status.CREATED)
        .header(HttpHeaders.LOCATION, uri.toString())
        .entity(body)
        .build();
  }

  @GET
  @Path("/{id}")
  public Response read(@PathParam("id") String id) {
    var doctor = new DoctorPublicDto(service.read(id));
    return Response.ok(doctor).build();
  }

  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") String id, DoctorDto body) {
    checkViolations(body);
    service.update(id, body);
    return Response.ok().build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") String id) {
    service.delete(id);
    return Response.ok().build();
  }

  private void checkViolations(DoctorDto body) {
    var violations = validator.validate(body);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
