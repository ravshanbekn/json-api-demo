package kz.app.jsonapidemo.controller;

import kz.app.jsonapidemo.model.data.BookData;
import kz.app.jsonapidemo.model.entity.Book;
import kz.app.jsonapidemo.model.jsonapi.JsonApiDocument;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import kz.app.jsonapidemo.serializer.BookSerializer;
import kz.app.jsonapidemo.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookSerializer bookSerializer;
    private final BookService bookService;

    @GetMapping
    public JsonApiDocument<?> getBooks() {
        List<Book> books = bookService.findAll();
        return new JsonApiDocument<>(bookSerializer.serializeAll(books));
    }

    @GetMapping("/{id}")
    public JsonApiDocument<?> getBookById(@PathVariable Long id) {
        Book book = bookService.findById(id);
        return new JsonApiDocument<>(bookSerializer.serialize(book));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JsonApiDocument<?> createBook(@RequestBody JsonApiDocument<ResourceObject> request) {
        Book book = bookSerializer.toEntity(request.getData());
        Book saved = bookService.create(book);
        return new JsonApiDocument<>(bookSerializer.serialize(saved));
    }

    @PatchMapping("/{id}")
    public JsonApiDocument<?> updateBook(@PathVariable Long id,
                                         @RequestBody JsonApiDocument<ResourceObject> request) {
        BookData bookData = bookSerializer.toData(request.getData());
        Book updated = bookService.update(id, bookData);
        return new JsonApiDocument<>(bookSerializer.serialize(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.delete(id);
    }

    // todo
    // --- Related endpoints ---

    @GetMapping("/{id}/author")
    public JsonApiDocument<?> getBookAuthor(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO");
    }

    @GetMapping("/{id}/genres")
    public JsonApiDocument<?> getBookGenres(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO");
    }

    // --- Relationship endpoints ---

    @GetMapping("/{id}/relationships/author")
    public JsonApiDocument<?> getAuthorRelationship(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO");
    }

    @PatchMapping("/{id}/relationships/author")
    public JsonApiDocument<?> updateAuthorRelationship(@PathVariable Long id,
                                                       @RequestBody JsonApiDocument<ResourceObject> request) {
        throw new UnsupportedOperationException("TODO");
    }

    @GetMapping("/{id}/relationships/genres")
    public JsonApiDocument<?> getGenresRelationship(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO");
    }

    @PostMapping("/{id}/relationships/genres")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addGenresRelationship(@PathVariable Long id,
                                      @RequestBody JsonApiDocument<ResourceObject> request) {
        throw new UnsupportedOperationException("TODO");
    }

    @PatchMapping("/{id}/relationships/genres")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void replaceGenresRelationship(@PathVariable Long id,
                                          @RequestBody JsonApiDocument<ResourceObject> request) {
        throw new UnsupportedOperationException("TODO");
    }

    @DeleteMapping("/{id}/relationships/genres")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGenresRelationship(@PathVariable Long id,
                                         @RequestBody JsonApiDocument<ResourceObject> request) {
        throw new UnsupportedOperationException("TODO");
    }
}
