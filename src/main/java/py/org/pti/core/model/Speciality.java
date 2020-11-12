package py.org.pti.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

@Entity
@Table(name = "speciality", schema = "public")
@NamedQuery(
    name = "Speciality.findAll",
    query = "SELECT s FROM Speciality s ORDER BY s.description",
    hints = @QueryHint(name = "org.hibernate.cacheable", value = "true"))
public class Speciality {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Integer id;

  @Column(name = "description")
  private String description;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "Speciality [id=" + id + ", description=" + description + "]";
  }
}
