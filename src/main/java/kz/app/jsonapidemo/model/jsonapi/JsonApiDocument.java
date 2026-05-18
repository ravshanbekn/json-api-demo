package kz.app.jsonapidemo.model.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonApiDocument<T> {

    private T data;
    private List<ErrorObject> errors;
    private Map<String, Object> meta;

    public JsonApiDocument(T data) {
        this.data = data;
    }
}
