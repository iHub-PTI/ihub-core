package py.org.pti.core.mapper;

import static org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem.EMAIL;
import static org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem.PHONE;

import com.ibm.icu.util.ULocale;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.BadRequestException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.StringType;
import py.org.pti.core.dto.DoctorDto;
import py.org.pti.core.model.Speciality;

@Dependent
public class DoctorsMapper {

  @Inject EntityManager em;

  public DoctorDto toDoctor(JsonWebToken jwt) {
    var dto = new DoctorDto();
    dto.identifier = jwt.getName();
    dto.givenName = jwt.getClaim("given_name");
    dto.familyName = jwt.getClaim("family_name");
    dto.gender = AdministrativeGender.UNKNOWN.toCode();
    dto.email = jwt.getClaim("email");
    if (jwt.getClaim("doctor_register_number") != null) {
      dto.license = jwt.getClaim("doctor_register_number").toString(); // this is a long
    }
    if (jwt.getClaim("birthdate") != null) {
      dto.birthDate =
          LocalDate.parse(jwt.getClaim("birthdate"), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    } else {
      dto.birthDate = LocalDate.EPOCH;
    }
    dto.specializations = new String[] {};
    dto.languages = new String[] {};
    return dto;
  }

  public DoctorDto toDoctor(Practitioner practitioner) {
    var dto = new DoctorDto();
    dto.id = getOrDefault(practitioner.getIdElement().getIdPart());
    dto.givenName = getOrDefault(practitioner.getNameFirstRep().getGivenAsSingleString());
    dto.familyName = getOrDefault(practitioner.getNameFirstRep().getFamily());
    for (var identifier : practitioner.getIdentifier()) {
      if (identifier.getSystem().equals(DoctorDto.IDENTIFIER_SYSTEM)) {
        dto.identifier = getOrDefault(identifier.getValue());
      }
      if (identifier.getSystem().equals(DoctorDto.LICENSE_IDENTIFIER_SYSTEM)) {
        dto.license = getOrDefault(identifier.getValue());
      } else {
        dto.license =
            getOrDefault(
                practitioner.getQualificationFirstRep().getIdentifierFirstRep().getValue());
      }
    }
    dto.addressDescription = practitioner.getAddressFirstRep().getText();
    dto.city = practitioner.getAddressFirstRep().getCity();
    if (practitioner.getAddressFirstRep().getLine().size() > 0) {
      dto.street = practitioner.getAddressFirstRep().getLine().get(0).toString();
    }
    if (practitioner.getAddressFirstRep().getLine().size() > 1) {
      dto.neighborhood = practitioner.getAddressFirstRep().getLine().get(1).toString();
    }
    if (practitioner.hasBirthDate()) {
      dto.birthDate =
          LocalDate.ofInstant(practitioner.getBirthDate().toInstant(), ZoneId.systemDefault());
    } else {
      dto.birthDate = LocalDate.EPOCH;
    }
    if (practitioner.hasExtension(DoctorDto.BIOGRAPHY_URL)) {
      dto.biography = practitioner.getExtensionByUrl(DoctorDto.BIOGRAPHY_URL).getValue().toString();
    }
    if (practitioner.hasExtension(DoctorDto.PHOTO_URL)) {
      dto.photoUrl = practitioner.getExtensionByUrl(DoctorDto.PHOTO_URL).getValue().toString();
    }
    if (practitioner.hasGender()) {
      dto.gender = practitioner.getGender().toCode();
    } else {
      dto.gender = "";
    }
    for (var contact : practitioner.getTelecom()) {
      if (contact.getSystem() == EMAIL) {
        dto.email = contact.getValue();
      } else {
        dto.email = "";
      }
      if (contact.getSystem() == PHONE) {
        dto.phone = contact.getValue();
      }
    }
    dto.languages =
        practitioner.getCommunication().stream()
            .flatMap(c -> c.getCoding().stream())
            .map(Coding::getCode)
            .toArray(String[]::new);
    dto.specializations =
        practitioner.getQualification().stream()
            .flatMap(q -> q.getCode().getCoding().stream())
            .map(Coding::getCode)
            .toArray(String[]::new);
    return dto;
  }

  public Practitioner toPractitioner(DoctorDto dto) {
    var practitioner = new Practitioner();
    practitioner.addIdentifier().setSystem(DoctorDto.IDENTIFIER_SYSTEM).setValue(dto.identifier);
    practitioner
        .addIdentifier()
        .setSystem(DoctorDto.LICENSE_IDENTIFIER_SYSTEM)
        .setValue(dto.license);
    practitioner.addName().setFamily(dto.familyName).addGiven(dto.givenName);
    practitioner.setGender(AdministrativeGender.fromCode(dto.gender));
    practitioner.addTelecom().setSystem(PHONE).setValue(dto.phone);
    practitioner.addTelecom().setSystem(EMAIL).setValue(dto.email);
    practitioner
        .addAddress()
        .addLine(dto.street)
        .addLine(dto.neighborhood)
        .setCity(dto.city)
        .setText(dto.addressDescription);
    practitioner.setBirthDate(
        Date.from(dto.birthDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    practitioner
        .addExtension()
        .setUrl(DoctorDto.BIOGRAPHY_URL)
        .setValue(new StringType(dto.biography));
    practitioner.addExtension().setUrl(DoctorDto.PHOTO_URL).setValue(new StringType(dto.photoUrl));
    for (var specialization : dto.specializations) {
      var name = getSpecialityName(specialization);
      var code = new Coding(DoctorDto.QUALIFICATION_SYSTEM, specialization, name);
      practitioner
          .addQualification()
          .setCode(new CodeableConcept(code))
          .addIdentifier()
          .setSystem(DoctorDto.QUALIFICATION_SYSTEM)
          .setValue(dto.license);
    }
    for (var language : dto.languages) {
      var name = ULocale.forLanguageTag(language).getDisplayLanguage();
      practitioner
          .addCommunication()
          .addCoding()
          .setSystem(DoctorDto.LANGUAGE_SYSTEM)
          .setCode(language)
          .setDisplay(name);
    }
    return practitioner;
  }

  private String getSpecialityName(String specialization) {
    var id = 0;
    try {
      id = Integer.parseInt(specialization);
    } catch (NumberFormatException e) {
      throw new BadRequestException("Invalid specialtyId");
    }
    var speciality = em.find(Speciality.class, id);
    if (speciality == null) {
      throw new BadRequestException("specialtyId does not exist");
    }
    return speciality.getDescription();
  }

  private String getOrDefault(String nullableValue) {
    return nullableValue == null ? "" : nullableValue;
  }
}
