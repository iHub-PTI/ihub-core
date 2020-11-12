package py.org.pti.core.dto;

import py.org.pti.core.model.Speciality;

public class SpecializationDto {

  public String id;
  public String description;

  public SpecializationDto(Speciality speciality) {
    this.id = speciality.getId().toString();
    this.description = speciality.getDescription();
  }
}
