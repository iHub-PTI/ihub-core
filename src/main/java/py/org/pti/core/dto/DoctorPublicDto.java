package py.org.pti.core.dto;

import java.time.LocalDate;
import javax.json.bind.annotation.JsonbTransient;

public class DoctorPublicDto extends DoctorDto {

  public DoctorPublicDto(DoctorDto dto) {
    this.givenName = dto.givenName;
    this.familyName = dto.familyName;
    this.birthDate = dto.birthDate;
    this.email = dto.email;
    this.gender = dto.gender;
    this.id = dto.id;
    this.identifier = dto.identifier;
    this.phone = dto.phone;
    this.street = dto.street;
    this.neighborhood = dto.neighborhood;
    this.city = dto.city;
    this.addressDescription = dto.addressDescription;
    this.biography = dto.biography;
    this.license = dto.license;
    this.photoUrl = dto.photoUrl;
    this.languages = dto.languages;
    this.specializations = dto.specializations;
  }

  @JsonbTransient
  public LocalDate getBirthDate() {
    return birthDate;
  }

  @JsonbTransient
  public String getEmail() {
    return email;
  }

  @JsonbTransient
  public String getGender() {
    return gender;
  }

  @JsonbTransient
  public String getPhone() {
    return phone;
  }
}
