package pl.jsql.api.model.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Type;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "application_id")
    public Application application;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    public User user;

    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    public String queryHash;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    public Date requestDate;

}
