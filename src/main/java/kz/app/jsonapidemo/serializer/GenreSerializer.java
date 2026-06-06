package kz.app.jsonapidemo.serializer;

import kz.app.jsonapidemo.model.data.GenreData;
import kz.app.jsonapidemo.model.entity.Genre;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class GenreSerializer implements ResourceSerializer<Genre> {

    public static final String TYPE = "genres";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ResourceObject serialize(Genre genre) {
        return serialize(genre, Map.of());
    }

    @Override
    public ResourceObject serialize(Genre genre, Map<String, Set<String>> fieldsets) {
        ResourceObject resourceObject = new ResourceObject();
        resourceObject.setType(TYPE);
        resourceObject.setId(String.valueOf(genre.getId()));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", genre.getName());
        attributes.put("description", genre.getDescription());

        Set<String> fields = fieldsets.getOrDefault("genres", null);
        if (fields != null) {
            attributes.keySet().retainAll(fields);
        }

        resourceObject.setAttributes(attributes);
        return resourceObject;
    }

    public Genre toEntity(ResourceObject resource) {
        Genre genre = new Genre();
        genre.setName((String) resource.getAttributes().get("name"));
        genre.setDescription((String) resource.getAttributes().get("description"));
        return genre;
    }

    public GenreData toData(ResourceObject resource) {
        GenreData genreData = new GenreData();
        genreData.setName((String) resource.getAttributes().get("name"));
        genreData.setDescription((String) resource.getAttributes().get("description"));
        return genreData;
    }
}
