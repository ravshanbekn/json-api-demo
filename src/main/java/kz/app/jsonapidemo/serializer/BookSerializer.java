package kz.app.jsonapidemo.serializer;

import kz.app.jsonapidemo.model.data.BookData;
import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.model.entity.Book;
import kz.app.jsonapidemo.model.entity.Genre;
import kz.app.jsonapidemo.model.jsonapi.LinkObject;
import kz.app.jsonapidemo.model.jsonapi.RelationshipObject;
import kz.app.jsonapidemo.model.jsonapi.ResourceIdentifier;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class BookSerializer implements ResourceSerializer<Book> {

    public static final String TYPE = "books";
    private static final String AUTHOR_REL = "author";
    private static final String GENRE_REL = "genres";

    @Override
    public String getType() {
        return TYPE;
    }

    public ResourceObject serialize(Book book, Map<String, Set<String>> fieldsets) {
        ResourceObject resourceObject = new ResourceObject();
        resourceObject.setType(TYPE);
        resourceObject.setId(String.valueOf(book.getId()));

        Set<String> fields = fieldsets.getOrDefault("books", null);
        Map<String, Object> attributes = getAttributes(book, fields);
        return getResourceObject(book, resourceObject, attributes);
    }

    @Override
    public ResourceObject serialize(Book book) {
        return serialize(book, Map.of());
    }

    public Book toEntity(ResourceObject resource) {
        Book book = new Book();
        book.setTitle((String) resource.getAttributes().get("title"));
        book.setSummary((String) resource.getAttributes().get("summary"));
        book.setIsbn((String) resource.getAttributes().get("isbn"));
        Object year = resource.getAttributes().get("publishedYear");
        if (year != null) book.setPublishedYear((Integer) year);
        return book;
    }

    public BookData toData(ResourceObject resource) {
        BookData bookData = new BookData();
        bookData.setTitle((String) resource.getAttributes().get("title"));
        bookData.setSummary((String) resource.getAttributes().get("summary"));
        bookData.setIsbn((String) resource.getAttributes().get("isbn"));
        Object year = resource.getAttributes().get("publishedYear");
        if (year != null) bookData.setPublishedYear((Integer) year);
        return bookData;
    }

    private static Map<String, Object> getAttributes(Book book, Set<String> fields) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("title", book.getTitle());
        attributes.put("summary", book.getSummary());
        attributes.put("publishedYear", book.getPublishedYear());
        attributes.put("isbn", book.getIsbn());

        if (!ObjectUtils.isEmpty(fields)) {
            attributes.keySet().retainAll(fields);
        }

        return attributes;
    }

    private static RelationshipObject<List<ResourceIdentifier>> getGenreIdentifiers(Book book, List<Genre> genres) {
        RelationshipObject<List<ResourceIdentifier>> genresRelationships = new RelationshipObject<>();

        List<ResourceIdentifier> genresIdentifiers = genres.stream()
                .map(genre -> new ResourceIdentifier(String.valueOf(genre.getId()), GenreSerializer.TYPE))
                .toList();

        genresRelationships.setData(genresIdentifiers);

        LinkObject link = new LinkObject();
        link.setSelf("/api/books/" + book.getId() + "/relationships/genres");
        link.setRelated("/api/books/" + book.getId() + "/genres");
        genresRelationships.setLinks(link);
        return genresRelationships;
    }

    private static RelationshipObject<ResourceIdentifier> getAuthorIdentifiers(Book book, Author author) {
        RelationshipObject<ResourceIdentifier> authorRelationships = new RelationshipObject<>();
        ResourceIdentifier identifier = new ResourceIdentifier(String.valueOf(author.getId()), AuthorSerializer.TYPE);

        authorRelationships.setData(identifier);

        LinkObject link = new LinkObject();
        link.setSelf("/api/books/" + book.getId() + "/relationships/author");
        link.setRelated("/api/books/" + book.getId() + "/author");
        authorRelationships.setLinks(link);
        return authorRelationships;
    }

    private static ResourceObject getResourceObject(Book book, ResourceObject resourceObject, Map<String, Object> attributes) {
        resourceObject.setAttributes(attributes);

        Map<String, Object> relationships = new HashMap<>();
        Author author = book.getAuthor();
        if (author != null) {
            RelationshipObject<ResourceIdentifier> authorRelationships = getAuthorIdentifiers(book, author);

            relationships.put(AUTHOR_REL, authorRelationships);
        }

        List<Genre> genres = book.getGenres();
        if (!ObjectUtils.isEmpty(genres)) {
            RelationshipObject<List<ResourceIdentifier>> genresRelationships = getGenreIdentifiers(book, genres);

            relationships.put(GENRE_REL, genresRelationships);
        }

        if (!relationships.isEmpty()) {
            resourceObject.setRelationships(relationships);
        }
        return resourceObject;
    }
}
