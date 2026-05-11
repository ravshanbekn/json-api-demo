package kz.app.jsonapidemo.model.jsonapi;

import lombok.Data;

import java.util.Map;

@Data
public class ResourceObject {

    private String type;
    private String id;
    private Map<String, Object> attributes;
    private Map<String, Object> relationships;
}
