package kz.app.jsonapidemo.model.jsonapi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonApiDocument<T> {

    private T data;
    private List<ResourceObject> included;
    private List<ErrorObject> errors;
    private Map<String, Object> meta;

    @JsonCreator
    public JsonApiDocument() {}

    public JsonApiDocument(T data) {
        this(data, null);
    }

    public JsonApiDocument(T data, List<ResourceObject> included) {
        this.data = data;
        this.included = included;
    }

    public JsonApiDocument(List<ErrorObject> errors) {
        this.errors = errors;
    }
}
