package pl.jsql.api.model.dict

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "application_language")
class ApplicationLanguageDict {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @Column(unique = true)
    @NotNull
    String name

    @Column(unique = true)
    @NotNull
    String value
}
