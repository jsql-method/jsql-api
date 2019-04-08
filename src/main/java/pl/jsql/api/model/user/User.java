package pl.jsql.api.model.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "role_id")
    public Role role;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id", nullable = true)
    public Company company;

    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    public String password;

    @Type(type = "org.hibernate.type.TextType")
    public String activationToken;

    public Boolean activated;

    @Column(unique = true)
    @NotNull
    public String email;

    @NotNull
    public String firstName;

    @NotNull
    public String lastName;

    @NotNull
    public Boolean accountExpired;

    @NotNull
    public Boolean accountLocked;

    @NotNull
    public Boolean passwordExpired;

    @Type(type = "org.hibernate.type.TextType")
    public String forgotToken;

    public Boolean blocked;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    public Date registerDate;

    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    public Date activationDate;

    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    public Date changePasswordDate;

    @NotNull
    public Boolean enabled;

    public Boolean isProductionDeveloper = false;

}
