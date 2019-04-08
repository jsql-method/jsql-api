package pl.jsql.api.model.hashing;

import com.fasterxml.jackson.annotation.JsonFormat;
import pl.jsql.api.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "query")
public class  Query {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "application_id")
    public Application application;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    public User user;

    @NotNull
    public String query;

    @NotNull
    public String hash;

    @NotNull
    public Boolean used;

    @NotNull
    public Boolean dynamic;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    public Date queryDate;

}
