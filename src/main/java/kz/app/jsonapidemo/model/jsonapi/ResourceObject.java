package kz.app.jsonapidemo.model.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceObject {

    private String type;
    private String id;
    private Map<String, Object> attributes;
    private Map<String, Object> relationships;
}
