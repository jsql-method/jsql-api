package pl.jsql.api.model.dict

import org.hibernate.annotations.Type
import pl.jsql.api.enums.SettingEnum

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "settings")
class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @Column(unique = true)
    @NotNull
    String name

    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    String value

    @Enumerated(EnumType.STRING)
    @NotNull
    SettingEnum type

}
