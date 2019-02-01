package pl.jsql.api.model.dict

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "encoding_enum")
class EncodingDict {
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
