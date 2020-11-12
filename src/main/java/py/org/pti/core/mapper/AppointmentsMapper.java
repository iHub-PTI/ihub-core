package py.org.pti.core.mapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import org.hl7.fhir.r4.model.Appointment;
import org.hl7.fhir.r4.model.Appointment.AppointmentStatus;
import org.hl7.fhir.r4.model.Appointment.ParticipationStatus;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import py.org.pti.core.dto.AppointmentDto;
import py.org.pti.core.dto.DoctorPublicDto;

@Dependent
public class AppointmentsMapper {

  @Inject PatientsMapper patientsMapper;

  @Inject DoctorsMapper doctorsMapper;

  public AppointmentDto toAppointmentM(Appointment appointment) {
    var dto = new AppointmentDto();
    dto.id = getOrDefault(appointment.getIdElement().getIdPart());
    dto.description = appointment.getDescription();
    dto.start = ZonedDateTime.ofInstant(appointment.getStart().toInstant(), ZoneId.systemDefault());
    dto.end = ZonedDateTime.ofInstant(appointment.getEnd().toInstant(), ZoneId.systemDefault());
    dto.patientId = getIdPart(appointment.getParticipant().get(0).getActor().getReference());
    dto.doctorId = getIdPart(appointment.getParticipant().get(1).getActor().getReference());
    for (var participant : appointment.getParticipant()) {
      var actor = participant.getActor().getResource();
      if (actor instanceof Patient) {
        dto.patient = patientsMapper.toPatientDto((Patient) actor);
      }
      if (actor instanceof Practitioner) {
        dto.doctor = new DoctorPublicDto(doctorsMapper.toDoctor((Practitioner) actor));
      }
    }
    return dto;
  }

  public Appointment toAppointment(AppointmentDto dto, Patient patient, Practitioner practitioner) {
    var appointment = new Appointment();
    appointment.setStatus(AppointmentStatus.BOOKED);
    appointment.setDescription(dto.description);
    appointment.setStart(Date.from(dto.start.toInstant()));
    appointment.setEnd(Date.from(dto.end.toInstant()));
    appointment
        .addParticipant()
        .setActor(new Reference("Patient/" + patient.getIdElement().getIdPart()))
        .setStatus(ParticipationStatus.ACCEPTED);
    appointment
        .addParticipant()
        .setActor(new Reference("Practitioner/" + practitioner.getIdElement().getIdPart()))
        .setStatus(ParticipationStatus.ACCEPTED);
    return appointment;
  }

  private String getIdPart(String reference) {
    if (reference == null) {
      throw new InternalServerErrorException("Reference cannot be null");
    }
    return reference.substring(reference.indexOf("/") + 1);
  }

  private String getOrDefault(String nullableValue) {
    return nullableValue == null ? "" : nullableValue;
  }
}
