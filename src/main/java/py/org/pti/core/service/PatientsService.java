package py.org.pti.core.service;

import static org.hl7.fhir.r4.model.Patient.IDENTIFIER;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import py.org.pti.core.dto.PatientDto;
import py.org.pti.core.mapper.PatientsMapper;

@Dependent
public class PatientsService {

  @Inject IGenericClient client;

  @Inject PatientsMapper mapper;

  public List<PatientDto> list() {
    return client
        .search()
        .forResource(Patient.class)
        .returnBundle(Bundle.class)
        .execute()
        .getEntry()
        .stream()
        .map(e -> (Patient) e.getResource())
        .map(p -> mapper.toPatientDto(p))
        .collect(Collectors.toList());
  }

  public String create(PatientDto dto) {
    var result =
        client
            .create()
            .resource(mapper.toPatient(dto))
            .conditional()
            .where(getIdentifierCondition(dto.identifier))
            .execute();
    return result.getId().getIdPart();
  }

  public PatientDto read(String id) {
    var patient = client.read().resource(Patient.class).withId(id).execute();
    return mapper.toPatientDto(patient);
  }

  public void update(String id, PatientDto dto) {
    var patient = mapper.toPatient(dto);
    patient.setId(new IdType("Patient", id));
    client.update().resource(patient).execute();
  }

  public void delete(String id) {
    var idType = new IdType("Patient", id);
    client.delete().resourceById(idType).execute();
  }

  public PatientDto readByIdentifier(String identifier) {
    var result =
        client
            .search()
            .forResource(Patient.class)
            .where(getIdentifierCondition(identifier))
            .returnBundle(Bundle.class)
            .execute();
    var resource = result.getEntryFirstRep().getResource();
    if (resource instanceof Patient) {
      return mapper.toPatientDto((Patient) resource);
    }
    return null;
  }

  public void updateByIdentifier(PatientDto dto) {
    var patient = mapper.toPatient(dto);
    client
        .update()
        .resource(patient)
        .conditional()
        .where(getIdentifierCondition(dto.identifier))
        .execute();
  }

  private ICriterion<TokenClientParam> getIdentifierCondition(String identifier) {
    return IDENTIFIER.exactly().systemAndIdentifier(PatientDto.IDENTIFIER_SYSTEM, identifier);
  }
}
