package pl.jsql.api.model.hashing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;
import pl.jsql.api.model.dict.DatabaseDialectDict;
import pl.jsql.api.model.dict.EncodingDict;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "options")
public class  Options {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "application_id")
    public Application application;

    @NotNull
    public Boolean encodeQuery;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "encoding_enum_id")
    public EncodingDict encodingAlgorithm;

    @NotNull
    public Boolean isSalt;

    @NotNull
    public String salt;

    @NotNull
    public Boolean saltBefore;

    @NotNull
    public Boolean saltAfter;

    @NotNull
    public Boolean saltRandomize;

    @NotNull
    public Boolean hashLengthLikeQuery;

    @NotNull
    public Integer hashMinLength;

    @NotNull
    public Integer hashMaxLength;

    @NotNull
    public Boolean removeQueriesAfterBuild;

    @Column
    @ColumnDefault("false")
    public Boolean allowedPlainQueries;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "database_dialect_dict_id")
    public DatabaseDialectDict databaseDialect;

    @NotNull
    public Boolean prod = false;

}
