package py.org.pti.core.resource;

import static java.util.stream.Collectors.toList;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import py.org.pti.core.dto.SpecializationDto;
import py.org.pti.core.service.SpecialityService;

@Path("/specializations")
@Produces(MediaType.APPLICATION_JSON)
public class SpecializationsResource {

  @Inject SpecialityService service;

  @GET
  public Response read() {
    var result = service.findAll().stream().map(SpecializationDto::new).collect(toList());
    return Response.ok(result).build();
  }
}
