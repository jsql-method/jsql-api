package pl.jsql.api.model.hashing

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.jsql.api.model.user.User

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "application")
class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @NotNull
    String apiKey

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    User user //company admin

    @NotNull
    String name

    Boolean active

    @JsonIgnore
    @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "developer_id")
    User developer

}
