package kz.app.jsonapidemo.controller;

import kz.app.jsonapidemo.model.data.AuthorData;
import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.model.jsonapi.JsonApiDocument;
import kz.app.jsonapidemo.model.jsonapi.ResourceIdentifier;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import kz.app.jsonapidemo.serializer.AuthorSerializer;
import kz.app.jsonapidemo.serializer.BookSerializer;
import kz.app.jsonapidemo.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorSerializer authorSerializer;
    private final AuthorService authorService;
    private final BookSerializer bookSerializer;

    @GetMapping
    public JsonApiDocument<?> getAuthors() {
        List<Author> authors = authorService.findAll();
        return new JsonApiDocument<>(authorSerializer.serializeAll(authors));
    }

    @GetMapping("/{id}")
    public JsonApiDocument<?> getAuthorById(@PathVariable Long id) {
        Author author = authorService.findById(id);
        return new JsonApiDocument<>(authorSerializer.serialize(author));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JsonApiDocument<?> createAuthor(@RequestBody JsonApiDocument<ResourceObject> authorRequest) {
        Author requestAuthor = authorSerializer.toEntity(authorRequest.getData());
        Author savedAuthor = authorService.create(requestAuthor);
        return new JsonApiDocument<>(authorSerializer.serialize(savedAuthor));
    }

    @PatchMapping("/{id}")
    public JsonApiDocument<?> updateAuthor(@PathVariable Long id,
                                           @RequestBody JsonApiDocument<ResourceObject> author) {
        AuthorData authorData = authorSerializer.toData(author.getData());
        Author updatedAuthor = authorService.update(id, authorData);
        return new JsonApiDocument<>(authorSerializer.serialize(updatedAuthor));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
    }

    @GetMapping("/{id}/books")
    public JsonApiDocument<?> getAuthorBooks(@PathVariable Long id) {
        Author author = authorService.findById(id);
        return new JsonApiDocument<>(bookSerializer.serializeAll(author.getBooks()));
    }

    @GetMapping("/{id}/relationships/books")
    public JsonApiDocument<?> getBooksRelationship(@PathVariable Long id) {
        Author author = authorService.findById(id);
        List<ResourceIdentifier> identifiers = author.getBooks().stream()
                .map(b -> new ResourceIdentifier(String.valueOf(b.getId()), BookSerializer.TYPE))
                .toList();
        return new JsonApiDocument<>(identifiers);
    }
}
