package pl.jsql.api.dto.request;

public class  OptionsRequest {

   public Boolean encodeQuery;
   public String encodingAlgorithm;
   public Boolean isSalt;
   public String salt;
   public Boolean saltBefore;
   public Boolean saltAfter;
   public Boolean saltRandomize;
   public Boolean hashLengthLikeQuery;
   public Integer hashMinLength;
   public Integer hashMaxLength;
   public Boolean removeQueriesAfterBuild;
   public String databaseDialect;
   public Boolean allowedPlainQueries;
   public Boolean prod;
}