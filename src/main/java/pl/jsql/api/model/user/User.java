package pl.jsql.api.model.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id", nullable = true)
    public Company company;

    @JsonIgnore
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    public String password;

    @JsonIgnore
    @Type(type = "org.hibernate.type.TextType")
    public String activationToken;

    @JsonIgnore
    public Boolean activated;

    @Column(unique = true)
    @NotNull
    public String email;

    @NotNull
    public String firstName;

    @NotNull
    public String lastName;

    @JsonIgnore
    @NotNull
    public Boolean accountExpired;

    @JsonIgnore
    @NotNull
    public Boolean accountLocked;

    @NotNull
    public Boolean passwordExpired;

    @JsonIgnore
    @Type(type = "org.hibernate.type.TextType")
    public String forgotToken;

    @JsonIgnore
    public Boolean blocked;

    @JsonIgnore
    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    public Date registerDate;

    @JsonIgnore
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    public Date activationDate;

    @JsonIgnore
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    public Date changePasswordDate;

    @JsonIgnore
    @NotNull
    public Boolean enabled;

    @JsonIgnore
    public Boolean isProductionDeveloper = false;

}
