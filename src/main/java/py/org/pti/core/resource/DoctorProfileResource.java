package py.org.pti.core.resource;

import io.quarkus.security.Authenticated;
import java.util.Objects;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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
import org.eclipse.microprofile.jwt.JsonWebToken;
import py.org.pti.core.dto.AppointmentDto;
import py.org.pti.core.dto.DoctorDto;
import py.org.pti.core.mapper.DoctorsMapper;
import py.org.pti.core.service.AppointmentsService;
import py.org.pti.core.service.DoctorsService;

@Path("/profile/doctor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class DoctorProfileResource {

  @Inject JsonWebToken jwt;

  @Inject DoctorsMapper mapper;

  @Inject DoctorsService doctorsService;

  @Inject AppointmentsService appointmentsService;

  @GET
  public Response readDoctor() {
    var doctor = getOrCreateDoctor();
    return Response.ok(doctor).build();
  }

  @PUT
  public Response updateDoctor(@Valid DoctorDto body) {
    body.identifier = jwt.getName();
    doctorsService.updateByIdentifier(body);
    return Response.ok().build();
  }

  @POST
  @Path("/appointments")
  public Response createAppointment(@Context UriInfo uriInfo, @Valid AppointmentDto body) {
    var doctor = getOrCreateDoctor();
    body.doctorId = doctor.id;
    var id = appointmentsService.create(body);
    var uri = UriBuilder.fromPath(uriInfo.getPath()).path(id).build();
    return Response.status(Status.CREATED)
        .header(HttpHeaders.LOCATION, uri.toString())
        .entity(body)
        .build();
  }

  @GET
  @Path("/appointments")
  public Response getAppointments(@QueryParam("include") String include) {
    var doctor = getOrCreateDoctor();
    var list = appointmentsService.list(doctor, include);
    return Response.ok(list).build();
  }

  @GET
  @Path("/appointments/{id}")
  public Response getAppointments(
      @PathParam("id") String id, @QueryParam("include") String include) {
    var doctor = getOrCreateDoctor();
    var appointment = appointmentsService.read(id, include);
    if (!Objects.equals(appointment.doctorId, doctor.id)) {
      throw new NotFoundException();
    }
    return Response.ok(appointment).build();
  }

  private DoctorDto getOrCreateDoctor() {
    var doctor = doctorsService.readByIdentifier(jwt.getName());
    if (doctor == null) {
      var dto = mapper.toDoctor(jwt);
      var id = doctorsService.create(dto);
      doctor = doctorsService.read(id);
    }
    return doctor;
  }
}
