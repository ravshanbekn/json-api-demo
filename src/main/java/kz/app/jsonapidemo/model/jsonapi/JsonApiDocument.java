package kz.app.jsonapidemo.model.jsonapi;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JsonApiDocument {

    private Object data;
    private List<ErrorObject> errors;
    private Map<String, Object> meta;
}
