package pl.jsql.api.model.stats

import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.user.User

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "build")
class Build {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "application_id")
    Application application

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    User user

    @NotNull
    Date hashingDate

    @NotNull
    Integer queriesCount

}
