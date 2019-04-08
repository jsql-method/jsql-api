package pl.jsql.api.model.hashing

import pl.jsql.api.model.user.User

import javax.persistence.*

@Entity
@Table(name = "application_members")
public class  ApplicationMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    User member

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "application_id")
    Application application

}
