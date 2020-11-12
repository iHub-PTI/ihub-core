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
import py.org.pti.core.dto.PatientDto;
import py.org.pti.core.mapper.PatientsMapper;
import py.org.pti.core.service.AppointmentsService;
import py.org.pti.core.service.PatientsService;

@Path("/profile/patient")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PatientProfileResource {

  @Inject JsonWebToken jwt;

  @Inject PatientsMapper mapper;

  @Inject PatientsService patientsService;

  @Inject AppointmentsService appointmentsService;

  @GET
  public Response readPatient() {
    var patient = getOrCreatePatient();
    return Response.ok(patient).build();
  }

  @PUT
  public Response updatePatient(@Valid PatientDto body) {
    body.identifier = jwt.getName();
    patientsService.updateByIdentifier(body);
    return Response.ok().build();
  }

  @POST
  @Path("/appointments")
  public Response createAppointment(@Context UriInfo uriInfo, @Valid AppointmentDto body) {
    var patient = getOrCreatePatient();
    body.patientId = patient.id;
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
    var patient = getOrCreatePatient();
    var list = appointmentsService.list(patient, include);
    return Response.ok(list).build();
  }

  @GET
  @Path("/appointments/{id}")
  public Response getAppointments(
      @PathParam("id") String id, @QueryParam("include") String include) {
    var patient = getOrCreatePatient();
    var appointment = appointmentsService.read(id, include);
    if (!Objects.equals(appointment.patientId, patient.id)) {
      throw new NotFoundException();
    }
    return Response.ok(appointment).build();
  }

  private PatientDto getOrCreatePatient() {
    var patient = patientsService.readByIdentifier(jwt.getName());
    if (patient == null) {
      var dto = mapper.toPatientDto(jwt);
      var id = patientsService.create(dto);
      patient = patientsService.read(id);
    }
    return patient;
  }
}
