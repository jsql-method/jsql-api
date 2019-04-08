package pl.jsql.api.model.user

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "avatar")
public class  Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @NotNull
    String name

    @NotNull
    String originalName

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user

}
