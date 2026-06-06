package kz.app.jsonapidemo.service;

import kz.app.jsonapidemo.exception.ObjectNotFoundException;
import kz.app.jsonapidemo.model.data.BookData;
import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.model.entity.Book;
import kz.app.jsonapidemo.model.entity.Genre;
import kz.app.jsonapidemo.model.jsonapi.JsonApiDocument;
import kz.app.jsonapidemo.model.jsonapi.ResourceIdentifier;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import kz.app.jsonapidemo.repository.AuthorRepository;
import kz.app.jsonapidemo.repository.BookRepository;
import kz.app.jsonapidemo.repository.GenreRepository;
import kz.app.jsonapidemo.serializer.BookSerializer;
import kz.app.jsonapidemo.serializer.IncludeResolver;
import kz.app.jsonapidemo.util.StringParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    private final BookSerializer bookSerializer;
    private final IncludeResolver includeResolver;

    @Transactional
    public JsonApiDocument<?> getBooks(Set<String> include, Map<String, String> fieldsets) {
        List<Book> books = findAll();
        Map<String, Set<String>> parsedFieldsets = StringParser.parse(fieldsets);
        List<ResourceObject> includes = includeResolver.resolve(books, include, parsedFieldsets);
        return new JsonApiDocument<>(bookSerializer.serializeAll(books, parsedFieldsets), includes);
    }

    @Transactional
    public JsonApiDocument<?> getBookById(Long id, Set<String> include, Map<String, Set<String>> fieldsets) {
        Book book = findById(id);
        List<ResourceObject> includes = includeResolver.resolve(List.of(book), include, fieldsets);
        return new JsonApiDocument<>(bookSerializer.serialize(book, fieldsets), includes);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Book not found with id: " + id));
    }

    @Transactional
    public Book create(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, BookData data) {
        Book book = findById(id);
        if (data.getTitle() != null) book.setTitle(data.getTitle());
        if (data.getSummary() != null) book.setSummary(data.getSummary());
        if (data.getIsbn() != null) book.setIsbn(data.getIsbn());
        if (data.getPublishedYear() != null) book.setPublishedYear(data.getPublishedYear());
        return bookRepository.save(book);
    }

    @Transactional
    public void updateAuthor(Long id, ResourceIdentifier resourceIdentifier) {
        if (resourceIdentifier == null || !"author".equals(resourceIdentifier.getType())) {
            throw new RuntimeException("Not correct author type");
        }
        Book book = findById(id);
        Author author = authorRepository.getReferenceById(Long.valueOf(resourceIdentifier.getId()));
        book.setAuthor(author);
    }

    @Transactional
    public void addGenres(Long id, List<ResourceIdentifier> identifiers) {
        Book book = findByIdWithGenres(id);
        Set<Long> existingIds = book.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        List<Long> newIds = identifiers.stream()
                .map(i -> Long.valueOf(i.getId()))
                .filter(genreId -> !existingIds.contains(genreId))
                .toList();
        book.getGenres().addAll(genreRepository.findAllById(newIds));
        bookRepository.save(book);
    }

    @Transactional
    public void replaceGenres(Long id, List<ResourceIdentifier> identifiers) {
        Book book = findByIdWithGenres(id);
        List<Long> ids = identifiers.stream()
                .map(i -> Long.valueOf(i.getId()))
                .toList();
        book.getGenres().clear();
        book.getGenres().addAll(genreRepository.findAllById(ids));
    }

    @Transactional
    public void removeGenres(Long id, List<ResourceIdentifier> identifiers) {
        Book book = findByIdWithGenres(id);
        Set<Long> idsToRemove = identifiers.stream()
                .map(i -> Long.valueOf(i.getId()))
                .collect(Collectors.toSet());
        book.getGenres().removeIf(g -> idsToRemove.contains(g.getId()));
    }

    public Book findByIdWithGenres(Long id) {
        return bookRepository.findWithGenresById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Book not found with id: " + id));
    }

    public List<Book> findByGenreId(Long genreId) {
        return bookRepository.findAllByGenres_Id(genreId);
    }

    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }
}
