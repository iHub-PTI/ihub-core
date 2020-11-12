package py.org.pti.core.resource;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import py.org.pti.core.dto.AppointmentDto;
import py.org.pti.core.service.AppointmentsService;

@Path("/appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppointmentsResource {

  @Inject AppointmentsService service;

  @GET
  public Response list(
      @QueryParam("doctors") String doctors,
      @QueryParam("patients") String patients,
      @QueryParam("include") String include) {
    var list = service.list(doctors, patients, include);
    return Response.ok(list).build();
  }

  @POST
  public Response create(@Context UriInfo uriInfo, @Valid AppointmentDto body) {
    var id = service.create(body);
    var uri = UriBuilder.fromPath(uriInfo.getPath()).path(id).build();
    return Response.status(Status.CREATED)
        .header(HttpHeaders.LOCATION, uri.toString())
        .entity(body)
        .build();
  }

  @GET
  @Path("/{id}")
  public Response read(@PathParam("id") String id, @QueryParam("include") String include) {
    var doctor = service.read(id, include);
    return Response.ok(doctor).build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") String id) {
    service.delete(id);
    return Response.ok().build();
  }
}
