package py.org.pti.core.mapper;

import static org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem.EMAIL;
import static org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem.PHONE;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.enterprise.context.Dependent;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import py.org.pti.core.dto.PatientDto;

@Dependent
public class PatientsMapper {

  public PatientDto toPatientDto(JsonWebToken jwt) {
    var dto = new PatientDto();
    dto.identifier = jwt.getName();
    dto.givenName = jwt.getClaim("given_name");
    dto.familyName = jwt.getClaim("family_name");
    dto.gender = AdministrativeGender.UNKNOWN.toCode();
    dto.email = jwt.getClaim("email");
    if (jwt.getClaim("birthdate") != null) {
      dto.birthDate =
          LocalDate.parse(jwt.getClaim("birthdate"), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    } else {
      dto.birthDate = LocalDate.EPOCH;
    }
    return dto;
  }

  public PatientDto toPatientDto(Patient patient) {
    var dto = new PatientDto();
    dto.id = getOrDefault(patient.getIdElement().getIdPart());
    dto.identifier = getOrDefault(patient.getIdentifierFirstRep().getValue());
    dto.givenName = getOrDefault(patient.getNameFirstRep().getGivenAsSingleString());
    dto.familyName = getOrDefault(patient.getNameFirstRep().getFamily());
    dto.addressDescription = patient.getAddressFirstRep().getText();
    dto.city = patient.getAddressFirstRep().getCity();
    if (patient.getAddressFirstRep().getLine().size() > 0) {
      dto.street = patient.getAddressFirstRep().getLine().get(0).toString();
    }
    if (patient.getAddressFirstRep().getLine().size() > 1) {
      dto.neighborhood = patient.getAddressFirstRep().getLine().get(1).toString();
    }
    if (patient.hasBirthDate()) {
      dto.birthDate =
          LocalDate.ofInstant(patient.getBirthDate().toInstant(), ZoneId.systemDefault());
    } else {
      dto.birthDate = LocalDate.EPOCH;
    }
    if (patient.hasExtension(PatientDto.JOB_URL)) {
      dto.job = patient.getExtensionByUrl(PatientDto.JOB_URL).getValue().toString();
    }
    if (patient.hasExtension(PatientDto.PHOTO_URL)) {
      dto.photoUrl = patient.getExtensionByUrl(PatientDto.PHOTO_URL).getValue().toString();
    }
    if (patient.hasGender()) {
      dto.gender = patient.getGender().toCode();
    } else {
      dto.gender = "";
    }
    for (var contact : patient.getTelecom()) {
      if (contact.getSystem() == EMAIL) {
        dto.email = contact.getValue();
      } else {
        dto.email = "";
      }
      if (contact.getSystem() == PHONE) {
        dto.phone = contact.getValue();
      }
    }
    return dto;
  }

  public Patient toPatient(PatientDto dto) {
    var patient = new Patient();
    patient.addIdentifier().setSystem(PatientDto.IDENTIFIER_SYSTEM).setValue(dto.identifier);
    patient.addName().setFamily(dto.familyName).addGiven(dto.givenName);
    patient.setGender(AdministrativeGender.fromCode(dto.gender));
    patient.addTelecom().setSystem(PHONE).setValue(dto.phone);
    patient.addTelecom().setSystem(EMAIL).setValue(dto.email);
    patient
        .addAddress()
        .addLine(dto.street)
        .addLine(dto.neighborhood)
        .setCity(dto.city)
        .setText(dto.addressDescription);
    patient.setBirthDate(Date.from(dto.birthDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    patient.addExtension().setUrl(PatientDto.JOB_URL).setValue(new StringType(dto.job));
    patient.addExtension().setUrl(PatientDto.PHOTO_URL).setValue(new StringType(dto.photoUrl));
    return patient;
  }

  private String getOrDefault(String nullableValue) {
    return nullableValue == null ? "" : nullableValue;
  }
}
