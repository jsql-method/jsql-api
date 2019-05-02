package pl.jsql.api.dto.request;

import pl.jsql.api.dto.response.DatabaseConnectionResponse;
import pl.jsql.api.enums.DatabaseDialectEnum;
import pl.jsql.api.enums.EncodingEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class  OptionsRequest {

   @NotNull(message = "${validation.message.notNull}")
   public EncodingEnum encodingAlgorithm;

   @NotNull(message = "${validation.message.notNull}")
   public Boolean isSalt;

   @NotNull(message = "${validation.message.notNull}")
   public String salt;

   @NotNull(message = "${validation.message.notNull}")
   public Boolean saltBefore;

   @NotNull(message = "${validation.message.notNull}")
   public Boolean saltAfter;

   @NotNull(message = "${validation.message.notNull}")
   public Boolean saltRandomize;

   @NotNull(message = "${validation.message.notNull}")
   public Boolean hashLengthLikeQuery;

   @NotNull(message = "${validation.message.notNull}")
   public Integer hashMinLength;

   @NotNull(message = "${validation.message.notNull}")
   public Integer hashMaxLength;

   @NotNull(message = "${validation.message.notNull}")
   public Boolean removeQueriesAfterBuild;

   @NotNull(message = "${validation.message.notNull}")
   public DatabaseDialectEnum databaseDialect;

   @NotNull(message = "${validation.message.notNull}")
   public Boolean allowedPlainQueries;

   @NotNull(message = "${validation.message.notNull}")
   public DatabaseConnectionRequest productionDatabaseOptions;

   @NotNull(message = "${validation.message.notNull}")
   public DatabaseConnectionRequest developerDatabaseOptions;

}