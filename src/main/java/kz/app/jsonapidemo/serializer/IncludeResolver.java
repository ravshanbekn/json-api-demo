package kz.app.jsonapidemo.serializer;

import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.model.entity.Book;
import kz.app.jsonapidemo.model.entity.Genre;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class IncludeResolver {

    private final AuthorSerializer authorSerializer;
    private final GenreSerializer genreSerializer;

    public List<ResourceObject> resolve(List<Book> books, Set<String> includes) {
        if (ObjectUtils.isEmpty(includes)) {
            return null;
        }

        Map<String, ResourceObject> result = new LinkedHashMap<>();

        if (includes.contains("author")) {
            for (Book book : books) {
                Author author = book.getAuthor();
                if (author != null) {
                    result.put("authors:" + author.getId(), authorSerializer.serialize(author));
                }
            }
        }

        if (includes.contains("genres")) {
            for (Book book : books) {
                for (Genre genre : book.getGenres()) {
                    result.put("genres:" + genre.getId(), genreSerializer.serialize(genre));
                }
            }
        }

        return result.isEmpty()
                ? null
                : new ArrayList<>(result.values());
    }
}
