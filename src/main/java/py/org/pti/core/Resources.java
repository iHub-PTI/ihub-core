package py.org.pti.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.logging.Logger;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class Resources {

  @ConfigProperty(name = "hapi-fhir-server.url")
  String serverUrl;

  @Produces
  public Logger produceLogger(InjectionPoint injectionPoint) {
    return Logger.getLogger(injectionPoint.getBean().getBeanClass().getSimpleName());
  }

  @Produces
  public IGenericClient getIGenericClient() {
    return FhirContext.forR4().newRestfulGenericClient(serverUrl);
  }
}
