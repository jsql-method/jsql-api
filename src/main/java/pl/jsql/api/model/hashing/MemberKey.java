package pl.jsql.api.model.hashing

import pl.jsql.api.model.user.User

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "member_key")
public class  MemberKey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    User user

    @NotNull
    String key

}
