package kz.app.jsonapidemo.serializer;

import kz.app.jsonapidemo.model.jsonapi.ResourceObject;

import java.util.Collection;
import java.util.List;

public interface ResourceSerializer<T> {

    String getType();

    ResourceObject serialize(T entity);

    default List<ResourceObject> serializeAll(Collection<T> entities) {
        return entities.stream()
                .map(this::serialize)
                .toList();
    }
}
