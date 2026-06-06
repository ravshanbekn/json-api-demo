package kz.app.jsonapidemo.controller;

import kz.app.jsonapidemo.model.data.BookData;
import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.model.entity.Book;
import kz.app.jsonapidemo.model.jsonapi.JsonApiDocument;
import kz.app.jsonapidemo.model.jsonapi.ResourceIdentifier;
import kz.app.jsonapidemo.model.jsonapi.ResourceIdentifierList;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import kz.app.jsonapidemo.serializer.AuthorSerializer;
import kz.app.jsonapidemo.serializer.BookSerializer;
import kz.app.jsonapidemo.serializer.GenreSerializer;
import kz.app.jsonapidemo.service.BookService;
import kz.app.jsonapidemo.util.StringParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookSerializer bookSerializer;
    private final AuthorSerializer authorSerializer;
    private final GenreSerializer genreSerializer;

    private final BookService bookService;

    @GetMapping
    public JsonApiDocument<?> getBooks(@RequestParam(required = false) Set<String> include,
                                       @RequestParam(required = false) Map<String, String> params) {
        Map<String, Set<String>> fieldsets = StringParser.parse(params);
        Map<String, String> filters = StringParser.getFilters(params);
        return bookService.getBooks(include, fieldsets, filters);
    }

    @GetMapping("/{id}")
    public JsonApiDocument<?> getBookById(@PathVariable Long id,
                                          @RequestParam(required = false) Set<String> include,
                                          @RequestParam(required = false) Map<String, String> params) {
        Map<String, Set<String>> fieldSets = StringParser.parse(params);
        return bookService.getBookById(id, include, fieldSets);
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

    @GetMapping("/{id}/author")
    public JsonApiDocument<?> getBookAuthor(@PathVariable Long id) {
        Book book = bookService.findById(id);
        Author author = book.getAuthor();
        if (author == null) {
            return new JsonApiDocument<>(null);
        }
        return new JsonApiDocument<>(authorSerializer.serialize(author));
    }

    @GetMapping("/{id}/genres")
    public JsonApiDocument<?> getBookGenres(@PathVariable Long id) {
        Book book = bookService.findByIdWithGenres(id);
        return new JsonApiDocument<>(genreSerializer.serializeAll(book.getGenres()));
    }

    @GetMapping("/{id}/relationships/author")
    public JsonApiDocument<?> getAuthorRelationship(@PathVariable Long id) {
        Book book = bookService.findById(id);
        Author author = book.getAuthor();
        if (author == null) {
            return new JsonApiDocument<>(null);
        }
        return new JsonApiDocument<>(new ResourceIdentifier(String.valueOf(author.getId()), AuthorSerializer.TYPE));
    }

    @PatchMapping("/{id}/relationships/author")
    public void updateAuthorRelationship(@PathVariable Long id,
                                         @RequestBody JsonApiDocument<ResourceIdentifier> request) {
        bookService.updateAuthor(id, request.getData());
    }

    @GetMapping("/{id}/relationships/genres")
    public JsonApiDocument<?> getGenresRelationship(@PathVariable Long id) {
        Book book = bookService.findByIdWithGenres(id);
        List<ResourceIdentifier> identifiers = book.getGenres().stream()
                .map(g -> new ResourceIdentifier(String.valueOf(g.getId()), GenreSerializer.TYPE))
                .toList();
        return new JsonApiDocument<>(identifiers);
    }

    @PostMapping("/{id}/relationships/genres")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addGenresRelationship(@PathVariable Long id,
                                      @RequestBody ResourceIdentifierList request) {
        bookService.addGenres(id, request.getData());
    }

    @PatchMapping("/{id}/relationships/genres")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void replaceGenresRelationship(@PathVariable Long id,
                                          @RequestBody ResourceIdentifierList request) {
        bookService.replaceGenres(id, request.getData());
    }

    @DeleteMapping("/{id}/relationships/genres")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGenresRelationship(@PathVariable Long id,
                                         @RequestBody ResourceIdentifierList request) {
        bookService.removeGenres(id, request.getData());
    }
}
