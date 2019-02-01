package pl.jsql.api.model.user

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Type

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "session")
class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    String sessionHash

    @NotNull
    String ipAddress

    @NotNull
    @Temporal(TemporalType.DATE)
    Date createdDate

    @Temporal(TemporalType.DATE)
    Date closedDate

}
