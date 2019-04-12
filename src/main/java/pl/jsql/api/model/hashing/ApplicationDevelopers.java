package pl.jsql.api.model.hashing;

import pl.jsql.api.model.user.User;

import javax.persistence.*;

@Entity
@Table(name = "application_developers")
public class ApplicationDevelopers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    public User developer;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "application_id")
    public Application application;

}
