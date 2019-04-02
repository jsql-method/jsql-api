package pl.jsql.api.model.payment

import com.fasterxml.jackson.annotation.JsonFormat
import pl.jsql.api.enums.PlansEnum
import pl.jsql.api.model.user.Company

import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name = "plans", uniqueConstraints = @UniqueConstraint(columnNames = "company_id"))
class Plans {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id")
    Company company

    @NotNull
    @Enumerated(EnumType.STRING)
    PlansEnum plan

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    Date activationDate

    Integer trialPeriod

    Boolean isTrial = false

    Boolean active = false

}
