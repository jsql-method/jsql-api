package pl.jsql.api.dto.response;

import pl.jsql.api.enums.DatabaseDialectEnum;
import pl.jsql.api.enums.EncodingEnum;

import java.util.List;

public class OptionsValuesResponse {

    public List<EncodingEnum> encodingAlgorithmValues;
    public List<DatabaseDialectEnum> databaseDialectValues;

}
