package pl.jsql.api.model.user

import com.fasterxml.jackson.annotation.JsonFormat

import javax.persistence.*

@Entity
@Table(name = "company")
class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    String name

    String street

    String city

    String postalCode

    String country

    boolean isLicensed

    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    Date creationDate

}
