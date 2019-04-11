package pl.jsql.api.dto.request;

import pl.jsql.api.enums.DatabaseDialectEnum;
import pl.jsql.api.enums.EncodingEnum;

public class  OptionsRequest {

   public EncodingEnum encodingAlgorithm;
   public Boolean isSalt;
   public String salt;
   public Boolean saltBefore;
   public Boolean saltAfter;
   public Boolean saltRandomize;
   public Boolean hashLengthLikeQuery;
   public Integer hashMinLength;
   public Integer hashMaxLength;
   public Boolean removeQueriesAfterBuild;
   public DatabaseDialectEnum databaseDialect;
   public Boolean allowedPlainQueries;
   public Boolean prod;
}