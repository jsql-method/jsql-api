package pl.jsql.api.model.hashing

import pl.jsql.api.model.user.User

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "query")
class Query {
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
    String query

    @NotNull
    String hash

    @NotNull
    Boolean used

    @NotNull
    Boolean dynamic

    @NotNull
    Date queryDate


}
