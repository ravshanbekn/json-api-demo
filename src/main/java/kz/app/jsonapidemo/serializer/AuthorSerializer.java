package kz.app.jsonapidemo.serializer;

import kz.app.jsonapidemo.model.data.AuthorData;
import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class AuthorSerializer implements ResourceSerializer<Author> {

    public static final String TYPE = "authors";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ResourceObject serialize(Author author) {
        return serialize(author, Map.of());
    }

    @Override
    public ResourceObject serialize(Author author, Map<String, Set<String>> fieldsets) {
        ResourceObject resourceObject = new ResourceObject();
        resourceObject.setType(TYPE);
        resourceObject.setId(String.valueOf(author.getId()));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", author.getFirstName());
        attributes.put("lastName", author.getLastName());
        attributes.put("bio", author.getBio());
        attributes.put("birthDate", author.getBirthDate());

        Set<String> fields = fieldsets.getOrDefault("authors", null);
        if (fields != null) {
            attributes.keySet().retainAll(fields);
        }

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

    public AuthorData toData(ResourceObject resource) {
        AuthorData authorData = new AuthorData();
        authorData.setFirstName((String) resource.getAttributes().get("firstName"));
        authorData.setLastName((String) resource.getAttributes().get("lastName"));
        authorData.setBio((String) resource.getAttributes().get("bio"));
        return authorData;
    }
}
