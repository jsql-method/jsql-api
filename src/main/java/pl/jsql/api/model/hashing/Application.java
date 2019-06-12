package pl.jsql.api.model.hashing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import pl.jsql.api.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "application")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @JsonIgnore
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    public String apiKey;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_admin_id")
    public User companyAdmin; //company admin

    @NotNull
    @Column(length = 250)
    public String name;

    @JsonIgnore
    public Boolean active;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "production_developer_id")
    public User productionDeveloper;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "development_developer_id")
    public User developmentDeveloper;

}
