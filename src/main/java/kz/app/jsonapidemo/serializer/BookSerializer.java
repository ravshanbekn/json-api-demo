package kz.app.jsonapidemo.serializer;

import kz.app.jsonapidemo.model.data.BookData;
import kz.app.jsonapidemo.model.entity.Book;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BookSerializer implements ResourceSerializer<Book> {

    private static final String TYPE = "books";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public ResourceObject serialize(Book book) {
        ResourceObject resourceObject = new ResourceObject();
        resourceObject.setType(TYPE);
        resourceObject.setId(String.valueOf(book.getId()));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("title", book.getTitle());
        attributes.put("summary", book.getSummary());
        attributes.put("publishedYear", book.getPublishedYear());
        attributes.put("isbn", book.getIsbn());

        resourceObject.setAttributes(attributes);
        return resourceObject;
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
}
