package py.org.pti.core.dto;

import java.time.ZonedDateTime;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.NotNull;

public class AppointmentDto {

  @NotNull(message = "start is required")
  @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  public ZonedDateTime start;

  @NotNull(message = "end is required")
  @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  public ZonedDateTime end;

  public String id;
  public String description;
  public String doctorId;
  public String patientId;
  public PatientDto patient;
  public DoctorPublicDto doctor;
}
