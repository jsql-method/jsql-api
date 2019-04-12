package pl.jsql.api.model.hashing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import pl.jsql.api.enums.DatabaseDialectEnum;
import pl.jsql.api.enums.EncodingEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "options")
public class Options {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "application_id")
    public Application application;

    public EncodingEnum encodingAlgorithm;

    @NotNull
    public Boolean isSalt;

    @Type(type = "org.hibernate.type.TextType")
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

    public DatabaseDialectEnum databaseDialect;

    @NotNull
    public Boolean prod = false;

}
