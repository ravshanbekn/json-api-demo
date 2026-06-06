package kz.app.jsonapidemo.controller;

import kz.app.jsonapidemo.model.data.GenreData;
import kz.app.jsonapidemo.model.entity.Genre;
import kz.app.jsonapidemo.model.jsonapi.JsonApiDocument;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import kz.app.jsonapidemo.serializer.GenreSerializer;
import kz.app.jsonapidemo.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreSerializer genreSerializer;
    private final GenreService genreService;

    @GetMapping("/{id}")
    public JsonApiDocument<?> getGenreById(@PathVariable Long id) {
        Genre genre = genreService.findById(id);
        return new JsonApiDocument<>(genreSerializer.serialize(genre));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JsonApiDocument<?> createGenre(@RequestBody JsonApiDocument<ResourceObject> request) {
        Genre genre = genreSerializer.toEntity(request.getData());
        Genre saved = genreService.create(genre);
        return new JsonApiDocument<>(genreSerializer.serialize(saved));
    }

    @PatchMapping("/{id}")
    public JsonApiDocument<?> updateGenre(@PathVariable Long id,
                                          @RequestBody JsonApiDocument<ResourceObject> request) {
        GenreData genreData = genreSerializer.toData(request.getData());
        Genre updated = genreService.update(id, genreData);
        return new JsonApiDocument<>(genreSerializer.serialize(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGenre(@PathVariable Long id) {
        genreService.delete(id);
    }
}
