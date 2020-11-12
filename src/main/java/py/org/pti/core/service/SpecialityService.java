package py.org.pti.core.service;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import py.org.pti.core.model.Speciality;

@Dependent
public class SpecialityService {

  @Inject EntityManager em;

  public List<Speciality> findAll() {
    return em.createNamedQuery("Speciality.findAll", Speciality.class).getResultList();
  }
}
