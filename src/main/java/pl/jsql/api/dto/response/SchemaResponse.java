package pl.jsql.api.dto.response;

import java.util.HashMap;

public class  SchemaResponse {

    public HashMap<String, HashMap<String, SchemaFieldResponse>> schema = new HashMap<>();
}
