package pl.jsql.api.model.user;

import pl.jsql.api.enums.RoleTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    public RoleTypeEnum authority;

}
