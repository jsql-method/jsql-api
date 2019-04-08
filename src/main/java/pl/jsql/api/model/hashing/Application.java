package pl.jsql.api.model.hashing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.jsql.api.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "application")
public class  Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @NotNull
    public String apiKey;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    public User user; //company admin

    @NotNull
    public String name;

    public Boolean active;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "developer_id")
    public User developer;

}
