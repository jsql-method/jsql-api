package pl.jsql.api.model.user

import com.fasterxml.jackson.annotation.JsonFormat
import org.hibernate.annotations.Type

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "`user`")
public class  User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "role_id")
    Role role

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id", nullable = true)
    Company company

    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    String password

    @Type(type = "org.hibernate.type.TextType")
    String activationToken

    Boolean activated

    @Column(unique = true)
    @NotNull
    String email

    @NotNull
    String firstName

    @NotNull
    String lastName

    @NotNull
    Boolean accountExpired

    @NotNull
    Boolean accountLocked

    @NotNull
    Boolean passwordExpired

    @Type(type = "org.hibernate.type.TextType")
    String forgotToken

    Boolean blocked

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    Date registerDate

    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    Date activationDate

    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    Date changePasswordDate

    @NotNull
    Boolean enabled

    Boolean isProductionDeveloper = false

//    /**
//     *   Method sets field to desired value by string
//     *
//     * @param field Field name to be set
//     * @param value Value object
//     */
//    void setField(String field, Object value) {
//        switch (field) {
//            case "role":
//                if (value instanceof Role) this.role = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.role.getpublic class ().getTypeName())
//                break
//
//            case "password":
//                if (value instanceof String) this.password = DigestUtils.sha256Hex(password)
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.password.getpublic class ().getTypeName())
//                break
//
//            case "activationToken":
//                if (value instanceof String) this.activationToken = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.activationToken.getpublic class ().getTypeName())
//                break
//
//            case "activated":
//                if (value instanceof Boolean) this.activated = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.activated.getpublic class ().getTypeName())
//                break
//
//            case "email":
//                if (value instanceof String) this.email = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.email.getpublic class ().getTypeName())
//                break
//
//            case "firstName":
//                if (value instanceof String) this.firstName = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.firstName.getpublic class ().getTypeName())
//                break
//
//            case "lastName":
//                if (value instanceof String) this.lastName = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.lastName.getpublic class ().getTypeName())
//                break
//
//            case "accountExpired":
//                if (value instanceof Boolean) this.accountExpired = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.accountExpired.getpublic class ().getTypeName())
//                break
//
//            case "accountLocked":
//                if (value instanceof Boolean) this.accountLocked = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.accountLocked.getpublic class ().getTypeName())
//                break
//
//            case "passwordExpired":
//                if (value instanceof Boolean) this.passwordExpired = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.passwordExpired.getpublic class ().getTypeName())
//                break
//
//            case "registerDate":
//                if (value instanceof Date) this.registerDate = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.registerDate.getpublic class ().getTypeName())
//                break
//
//            case "enabled":
//                if (value instanceof Date) this.enabled = value
//                else throw new IllegalArgumentException("Could not assign object of type " + value.getpublic class ().getTypeName() + " to field '" + field + "' of type " + this.enabled.getpublic class ().getTypeName())
//                break
//
//            default:
//                throw new IllegalArgumentException("Field '" + field + "' does not exist.")
//                break
//        }
//    }

}
