package pl.jsql.api.model.user

import pl.jsql.api.enums.RoleTypeEnum

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

    @Temporal(TemporalType.DATE)
    Date creationDate

}
