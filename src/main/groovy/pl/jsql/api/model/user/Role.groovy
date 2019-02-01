package pl.jsql.api.model.user

import pl.jsql.api.enums.RoleTypeEnum

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "role")
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @NotNull
    RoleTypeEnum authority
}
