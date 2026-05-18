package kz.app.jsonapidemo.mapper;

import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthorMapper {

    private static final String type = "authors";

    public List<ResourceObject> toResourceObjects(List<Author> authors) {
        return authors.stream()
                .map(this::toResourceObject)
                .toList();
    }

    public ResourceObject toResourceObject(Author author) {
        ResourceObject resourceObject = new ResourceObject();
        resourceObject.setType(type);
        resourceObject.setId(String.valueOf(author.getId()));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", author.getFirstName());
        attributes.put("lastName", author.getLastName());
        attributes.put("bio", author.getBio());
        attributes.put("birthDate", author.getBirthDate());

        resourceObject.setAttributes(attributes);
        return resourceObject;
    }

    public Author toEntity(ResourceObject resource) {
        Author author = new Author();
        author.setFirstName((String) resource.getAttributes().get("firstName"));
        author.setLastName((String) resource.getAttributes().get("lastName"));
        author.setBio((String) resource.getAttributes().get("bio"));
        author.setBirthDate(LocalDate.parse((String) resource.getAttributes().get("birthDate")));
        return author;
    }
}
