package kz.app.jsonapidemo.serializer;

import kz.app.jsonapidemo.model.jsonapi.ResourceObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ResourceSerializer<T> {

    String getType();

    ResourceObject serialize(T entity);

    ResourceObject serialize(T entity, Map<String, Set<String>> fieldsets);

    default List<ResourceObject> serializeAll(Collection<T> entities) {
        return serializeAll(entities, Map.of());
    }

    default List<ResourceObject> serializeAll(Collection<T> entities, Map<String, Set<String>> fieldsets) {
        return entities.stream()
                .map(entity -> serialize(entity, fieldsets))
                .toList();
    }
}
