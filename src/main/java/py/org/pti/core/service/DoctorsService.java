package py.org.pti.core.service;

import static org.hl7.fhir.r4.model.Practitioner.IDENTIFIER;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
import py.org.pti.core.dto.DoctorDto;
import py.org.pti.core.mapper.DoctorsMapper;

@Dependent
public class DoctorsService {

  @Inject IGenericClient client;

  @Inject DoctorsMapper mapper;

  public List<DoctorDto> list(String content) {
    Bundle bundle;
    if (content != null && !content.isEmpty()) {
      var url = "Practitioner?_content=" + content.replace(" ", ",");
      bundle = client.search().byUrl(url).returnBundle(Bundle.class).execute();
    } else {
      bundle = client.search().forResource(Practitioner.class).returnBundle(Bundle.class).execute();
    }
    return bundle.getEntry().stream()
        .map(e -> (Practitioner) e.getResource())
        .map(practitioner -> mapper.toDoctor(practitioner))
        .collect(Collectors.toList());
  }

  public String create(DoctorDto dto) {
    var result =
        client
            .create()
            .resource(mapper.toPractitioner(dto))
            .conditional()
            .where(getIdentifierCondition(dto.identifier))
            .execute();
    return result.getId().getIdPart();
  }

  public DoctorDto read(String id) {
    var practitioner = client.read().resource(Practitioner.class).withId(id).execute();
    return mapper.toDoctor(practitioner);
  }

  public void update(String id, DoctorDto dto) {
    var practitioner = mapper.toPractitioner(dto);
    practitioner.setId(new IdType("Practitioner", id));
    client.update().resource(practitioner).execute();
  }

  public void delete(String id) {
    var idType = new IdType("Practitioner", id);
    client.delete().resourceById(idType).execute();
  }

  public DoctorDto readByIdentifier(String identifier) {
    var result =
        client
            .search()
            .forResource(Practitioner.class)
            .where(getIdentifierCondition(identifier))
            .returnBundle(Bundle.class)
            .execute();
    var resource = result.getEntryFirstRep().getResource();
    if (resource instanceof Practitioner) {
      return mapper.toDoctor((Practitioner) resource);
    }
    return null;
  }

  public void updateByIdentifier(DoctorDto dto) {
    var practitioner = mapper.toPractitioner(dto);
    client
        .update()
        .resource(practitioner)
        .conditional()
        .where(getIdentifierCondition(dto.identifier))
        .execute();
  }

  private ICriterion<TokenClientParam> getIdentifierCondition(String identifier) {
    return IDENTIFIER.exactly().systemAndIdentifier(DoctorDto.IDENTIFIER_SYSTEM, identifier);
  }
}
