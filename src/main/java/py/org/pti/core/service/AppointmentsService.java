package py.org.pti.core.service;

import static java.util.stream.Collectors.toList;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import org.hl7.fhir.r4.model.Appointment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import py.org.pti.core.dto.AppointmentDto;
import py.org.pti.core.dto.DoctorDto;
import py.org.pti.core.dto.PatientDto;
import py.org.pti.core.mapper.AppointmentsMapper;

@Dependent
public class AppointmentsService {

  @Inject IGenericClient client;

  @Inject AppointmentsMapper mapper;

  public List<AppointmentDto> list(String doctors, String patients, String include) {
    if (doctors != null && patients != null) {
      var ids = new ArrayList<String>();
      ids.addAll(List.of(doctors.split(",")));
      ids.addAll(List.of(patients.split(",")));
      return list(Appointment.ACTOR.hasAnyOfIds(ids), include);
    }
    if (doctors != null) {
      return list(Appointment.PRACTITIONER.hasAnyOfIds(List.of(doctors.split(","))), include);
    }
    if (patients != null) {
      return list(Appointment.PATIENT.hasAnyOfIds(List.of(patients.split(","))), include);
    }
    return list((ICriterion<?>) null, include);
  }

  public List<AppointmentDto> list(DoctorDto dto, String include) {
    return list(Appointment.PRACTITIONER.hasId(dto.id), include);
  }

  public List<AppointmentDto> list(PatientDto dto, String include) {
    return list(Appointment.PATIENT.hasId(dto.id), include);
  }

  private List<AppointmentDto> list(ICriterion<?> criterion, String include) {
    var query = client.search().forResource(Appointment.class).returnBundle(Bundle.class);
    if (criterion != null) {
      query.where(criterion);
    }
    if (include != null) {
      var includes = List.of(include.split(","));
      if (includes.contains("patient")) {
        query.include(Appointment.INCLUDE_PATIENT);
      }
      if (includes.contains("doctor")) {
        query.include(Appointment.INCLUDE_PRACTITIONER);
      }
    }
    return query.execute().getEntry().stream()
        .filter(e -> e.getResource() instanceof Appointment)
        .map(e -> (Appointment) e.getResource())
        .map(a -> mapper.toAppointmentM(a))
        .collect(toList());
  }

  public String create(AppointmentDto dto) {
    var doctor = getPractitioner(dto);
    var patient = getPatient(dto);
    var result = client.create().resource(mapper.toAppointment(dto, patient, doctor)).execute();
    return result.getId().getIdPart();
  }

  private Practitioner getPractitioner(AppointmentDto dto) {
    if (dto.doctorId == null || dto.doctorId.isBlank()) {
      throw new BadRequestException("doctorId is required");
    }
    try {
      return client.read().resource(Practitioner.class).withId(dto.doctorId).execute();
    } catch (ResourceNotFoundException e) {
      throw new BadRequestException("doctorId \"" + dto.doctorId + "\" does not exist");
    }
  }

  private Patient getPatient(AppointmentDto dto) {
    if (dto.patientId == null || dto.patientId.isBlank()) {
      throw new BadRequestException("patientId is required");
    }
    try {
      return client.read().resource(Patient.class).withId(dto.patientId).execute();
    } catch (ResourceNotFoundException e) {
      throw new BadRequestException("patientId \"" + dto.patientId + "\" does not exist");
    }
  }

  public AppointmentDto read(String id, String include) {
    var appointments = list(Appointment.RES_ID.exactly().code(id), include);
    if (appointments.isEmpty()) {
      throw new NotFoundException();
    }
    return appointments.get(0);
  }

  public void delete(String id) {
    var idType = new IdType("Appointment", id);
    client.delete().resourceById(idType).execute();
  }
}
