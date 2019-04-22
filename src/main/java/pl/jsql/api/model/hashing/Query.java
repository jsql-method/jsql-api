package pl.jsql.api.model.hashing;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Type;
import pl.jsql.api.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "query")
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "application_id")
    public Application application;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    public User user;

    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    public String query;

    @Type(type = "org.hibernate.type.TextType")
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
