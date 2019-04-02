package pl.jsql.api.model.hashing

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.ColumnDefault
import pl.jsql.api.model.dict.DatabaseDialectDict
import pl.jsql.api.model.dict.EncodingDict

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "options")
class Options {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "application_id")
    Application application

    @NotNull
    boolean encodeQuery

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "encoding_enum_id")
    EncodingDict encodingAlgorithm

    @NotNull
    boolean isSalt

    @NotNull
    String salt

    @NotNull
    boolean saltBefore

    @NotNull
    boolean saltAfter

    @NotNull
    boolean saltRandomize

    @NotNull
    boolean hashLengthLikeQuery

    @NotNull
    Integer hashMinLength

    @NotNull
    Integer hashMaxLength

    @NotNull
    boolean removeQueriesAfterBuild

    @Column
    @ColumnDefault("false")
    boolean allowedPlainQueries

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "database_dialect_dict_id")
    DatabaseDialectDict databaseDialect

    @NotNull
    boolean prod = false

}
