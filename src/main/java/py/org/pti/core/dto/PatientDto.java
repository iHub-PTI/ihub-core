package py.org.pti.core.dto;

import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PatientDto {

  public static String IDENTIFIER_SYSTEM = "https://www.policianacional.gov.py/identificaciones/";
  public static String JOB_URL = "https://www.pti.org.py/healt-core/extensions#job";
  public static String PHOTO_URL = "https://www.pti.org.py/healt-core/extensions#photoUrl";

  // required fields
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

  // non-required fields
  public String id;
  public String identifier;
  public String phone;
  public String street;
  public String neighborhood;
  public String city;
  public String addressDescription;
  public String job;
  public String photoUrl;
}
