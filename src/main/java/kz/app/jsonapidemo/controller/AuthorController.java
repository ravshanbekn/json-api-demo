package kz.app.jsonapidemo.controller;

import kz.app.jsonapidemo.mapper.AuthorMapper;
import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.model.jsonapi.JsonApiDocument;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import kz.app.jsonapidemo.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorMapper authorMapper;
    private final AuthorService authorService;

    @GetMapping
    public JsonApiDocument<?> getAuthor() {
        List<Author> authors = authorService.findAll();
        return new JsonApiDocument<>(authorMapper.toResourceObjects(authors));
    }

    @GetMapping("/{id}")
    public JsonApiDocument<?> getAuthorById(@PathVariable Long id) {
        Author author = authorService.findById(id);
        return new JsonApiDocument<>(authorMapper.toResourceObject(author));
    }

    @PostMapping
    public JsonApiDocument<?> createAuthor(@RequestBody JsonApiDocument<ResourceObject> authorRequest) {
        Author author = authorService.create(authorRequest.getData());
        return new JsonApiDocument<>(authorMapper.toResourceObject(author));
    }

    @PatchMapping("/{id}")
    public JsonApiDocument<?> updateAuthor(@PathVariable String id, @RequestBody JsonApiDocument author) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable String id) {
    }
}
