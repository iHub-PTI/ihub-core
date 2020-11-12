package py.org.pti.core.resource;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import py.org.pti.core.dto.PatientDto;
import py.org.pti.core.service.PatientsService;

@Path("/patients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientsResource {

  @Inject PatientsService service;

  @GET
  public Response list() {
    var list = service.list();
    return Response.ok(list).build();
  }

  @POST
  public Response create(@Context UriInfo uriInfo, @Valid PatientDto body) {
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
    var patient = service.read(id);
    return Response.ok(patient).build();
  }

  @PUT
  @Path("/{id}")
  public Response update(@PathParam("id") String id, @Valid PatientDto body) {
    service.update(id, body);
    return Response.ok().build();
  }

  @DELETE
  @Path("/{id}")
  public Response delete(@PathParam("id") String id) {
    service.delete(id);
    return Response.ok().build();
  }
}
