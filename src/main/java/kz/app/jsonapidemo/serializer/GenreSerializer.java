package kz.app.jsonapidemo.serializer;

import kz.app.jsonapidemo.model.data.GenreData;
import kz.app.jsonapidemo.model.entity.Genre;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GenreSerializer implements ResourceSerializer<Genre> {

    private static final String TYPE = "genres";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ResourceObject serialize(Genre genre) {
        ResourceObject resourceObject = new ResourceObject();
        resourceObject.setType(TYPE);
        resourceObject.setId(String.valueOf(genre.getId()));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", genre.getName());
        attributes.put("description", genre.getDescription());

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
