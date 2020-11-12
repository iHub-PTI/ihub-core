package py.org.pti.core.dto;

import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class DoctorDto {

  public static String LANGUAGE_SYSTEM = "urn:ietf:bcp:47";
  public static String QUALIFICATION_SYSTEM = "https://www.mspbs.gov.py/especialidades/";
  public static String IDENTIFIER_SYSTEM = "https://www.policianacional.gov.py/identificaciones/";
  public static String LICENSE_IDENTIFIER_SYSTEM = "http://sirepro.mspbs.gov.py/";
  public static String BIOGRAPHY_URL = "https://www.pti.org.py/healt-core/extensions#biography";
  public static String PHOTO_URL = "https://www.pti.org.py/healt-core/extensions#photoUrl";

  @NotBlank(message = "givenName is required")
  public String givenName;

  @NotBlank(message = "familyName is required")
  public String familyName;

  @NotNull(message = "birthDate is required")
  public LocalDate birthDate;

  @NotBlank(message = "email is required")
  @Email(message = "wrong email format")
  public String email;

  @NotBlank(message = "gender is required")
  @Pattern(regexp = "male|female|other|unknown", message = "wrong gender format")
  public String gender;

  @NotNull(message = "languages is required")
  @NotEmpty(message = "languages must not be empty")
  public String[] languages;

  @NotNull(message = "specializations is required")
  @NotEmpty(message = "specializations must not be empty")
  public String[] specializations;

  // non-required fields
  public String id;
  public String identifier;
  public String phone;
  public String street;
  public String neighborhood;
  public String city;
  public String addressDescription;
  public String biography;
  public String license;
  public String photoUrl;
}
