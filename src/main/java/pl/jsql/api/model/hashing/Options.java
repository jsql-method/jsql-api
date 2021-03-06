package pl.jsql.api.model.hashing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import pl.jsql.api.enums.DatabaseDialectEnum;
import pl.jsql.api.enums.EncodingEnum;
import pl.jsql.api.utils.TokenUtil;

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

    @Enumerated(EnumType.STRING)
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

    public Boolean allowedPlainQueries;

    @Enumerated(EnumType.STRING)
    public DatabaseDialectEnum databaseDialect;

    @NotNull
    public Boolean prodCache = true;

    @NotNull
    public String randomSaltBefore = TokenUtil.randomSalt();

    @NotNull
    public String randomSaltAfter = TokenUtil.randomSalt();

    @NotNull
    public Integer prodDatabaseConnectionTimeout;

    public String prodDatabaseConnectionUrl;

    public String prodDatabaseConnectionUsername;

    public String prodDatabaseConnectionPassword;

    @NotNull
    public Integer devDatabaseConnectionTimeout;

    public String devDatabaseConnectionUrl;

    public String devDatabaseConnectionUsername;

    public String devDatabaseConnectionPassword;

    public Boolean purgeOptions = false;

    public Boolean purgeQueries = false;

}
