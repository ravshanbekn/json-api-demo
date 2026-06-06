package kz.app.jsonapidemo.controller;

import kz.app.jsonapidemo.AbstractIntegrationTest;
import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.model.entity.Book;
import kz.app.jsonapidemo.model.entity.Genre;
import kz.app.jsonapidemo.repository.AuthorRepository;
import kz.app.jsonapidemo.repository.BookRepository;
import kz.app.jsonapidemo.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookControllerTest extends AbstractIntegrationTest {

    private static final String CONTENT_TYPE = "application/vnd.api+json";
    private static final String BASE_URL = "/api/v1/books";

    @Autowired private MockMvc mockMvc;
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private GenreRepository genreRepository;

    private Author author;
    private Genre sciFiGenre;
    private Genre fantasyGenre;
    private Book book;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        author = new Author();
        author.setFirstName("Frank");
        author.setLastName("Herbert");
        author.setBirthDate(LocalDate.of(1920, 10, 8));
        author = authorRepository.save(author);

        sciFiGenre = new Genre();
        sciFiGenre.setName("Sci-Fi");
        sciFiGenre.setDescription("Science fiction");
        sciFiGenre = genreRepository.save(sciFiGenre);

        fantasyGenre = new Genre();
        fantasyGenre.setName("Fantasy");
        fantasyGenre.setDescription("Fantasy genre");
        fantasyGenre = genreRepository.save(fantasyGenre);

        book = new Book();
        book.setTitle("Dune");
        book.setSummary("A desert planet story");
        book.setPublishedYear(1965);
        book.setIsbn("978-0-441-17271-9");
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(List.of(sciFiGenre)));
        book = bookRepository.save(book);
    }

    // ─── GET /api/v1/books ──────────────────────────────────────────────

    @Test
    void getBooks_returnsCorrectJsonApiFormat() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].type").value("books"))
                .andExpect(jsonPath("$.data[0].id").value(book.getId().toString()))
                .andExpect(jsonPath("$.data[0].attributes.title").value("Dune"))
                .andExpect(jsonPath("$.data[0].attributes.isbn").value("978-0-441-17271-9"))
                .andExpect(jsonPath("$.data[0].attributes.publishedYear").value(1965))
                .andExpect(jsonPath("$.meta.totalElements").value(1))
                .andExpect(jsonPath("$.meta.number").value(0))
                .andExpect(jsonPath("$.meta.size").value(20))
                .andExpect(jsonPath("$.meta.totalPages").value(1));
    }

    @Test
    void getBooks_withTwoBooks_returnsAll() throws Exception {
        Book foundation = makeBook("Foundation", 1951, "978-0-553-29335-7", null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.meta.totalElements").value(2));

        // verify both IDs appear in response
        mockMvc.perform(get(BASE_URL))
                .andExpect(jsonPath("$.data[?(@.id == '" + book.getId() + "')]").exists())
                .andExpect(jsonPath("$.data[?(@.id == '" + foundation.getId() + "')]").exists());
    }

    @Test
    void getBooks_withPagination_firstPage() throws Exception {
        makeBook("Foundation", 1951, null, null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL)
                        .param("page[number]", "0")
                        .param("page[size]", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.meta.totalElements").value(2))
                .andExpect(jsonPath("$.meta.totalPages").value(2))
                .andExpect(jsonPath("$.meta.number").value(0))
                .andExpect(jsonPath("$.meta.size").value(1));
    }

    @Test
    void getBooks_withPagination_secondPage_returnsDifferentBook() throws Exception {
        // Sort by title ascending so order is deterministic
        makeBook("Foundation", 1951, null, null, new ArrayList<>());

        // page 0 → "Dune", page 1 → "Foundation" (alphabetical default isn't guaranteed without sort)
        // Use sort to ensure deterministic order
        mockMvc.perform(get(BASE_URL)
                        .param("sort", "title")
                        .param("page[number]", "0")
                        .param("page[size]", "1"))
                .andExpect(jsonPath("$.data[0].attributes.title").value("Dune"));

        mockMvc.perform(get(BASE_URL)
                        .param("sort", "title")
                        .param("page[number]", "1")
                        .param("page[size]", "1"))
                .andExpect(jsonPath("$.data[0].attributes.title").value("Foundation"));
    }

    @Test
    void getBooks_sortDescendingByYear_returnsNewerFirst() throws Exception {
        makeBook("Foundation", 1951, null, null, new ArrayList<>());
        makeBook("Neuromancer", 1984, null, null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL).param("sort", "-publishedYear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].attributes.title").value("Neuromancer"))
                .andExpect(jsonPath("$.data[1].attributes.title").value("Dune"))
                .andExpect(jsonPath("$.data[2].attributes.title").value("Foundation"));
    }

    @Test
    void getBooks_sortAscendingByTitle_returnsAlphabetical() throws Exception {
        makeBook("Foundation", 1951, null, null, new ArrayList<>());
        makeBook("Neuromancer", 1984, null, null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL).param("sort", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].attributes.title").value("Dune"))
                .andExpect(jsonPath("$.data[1].attributes.title").value("Foundation"))
                .andExpect(jsonPath("$.data[2].attributes.title").value("Neuromancer"));
    }

    @Test
    void getBooks_filterByTitle_returnsOnlyMatching() throws Exception {
        makeBook("Foundation", 1951, null, null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL).param("filter[title]", "Dune"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(book.getId().toString()))
                .andExpect(jsonPath("$.data[0].attributes.title").value("Dune"))
                .andExpect(jsonPath("$.meta.totalElements").value(1));
    }

    @Test
    void getBooks_filterByTitle_caseInsensitive() throws Exception {
        makeBook("Foundation", 1951, null, null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL).param("filter[title]", "dune"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].attributes.title").value("Dune"));
    }

    @Test
    void getBooks_filterByTitle_noMatch_returnsEmptyList() throws Exception {
        mockMvc.perform(get(BASE_URL).param("filter[title]", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.meta.totalElements").value(0));
    }

    @Test
    void getBooks_sparseFieldsets_returnsOnlyRequestedFields() throws Exception {
        mockMvc.perform(get(BASE_URL).param("fields[books]", "title,isbn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].attributes.title").value("Dune"))
                .andExpect(jsonPath("$.data[0].attributes.isbn").value("978-0-441-17271-9"))
                .andExpect(jsonPath("$.data[0].attributes.summary").doesNotExist())
                .andExpect(jsonPath("$.data[0].attributes.publishedYear").doesNotExist());
    }

    @Test
    void getBooks_sparseFieldsets_singleField() throws Exception {
        mockMvc.perform(get(BASE_URL).param("fields[books]", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].attributes.title").value("Dune"))
                .andExpect(jsonPath("$.data[0].attributes.isbn").doesNotExist())
                .andExpect(jsonPath("$.data[0].attributes.summary").doesNotExist())
                .andExpect(jsonPath("$.data[0].attributes.publishedYear").doesNotExist());
    }

    @Test
    void getBooks_includeAuthor_includedMatchesRelationship() throws Exception {
        mockMvc.perform(get(BASE_URL).param("include", "author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.included").isArray())
                .andExpect(jsonPath("$.included.length()").value(1))
                .andExpect(jsonPath("$.included[0].type").value("authors"))
                .andExpect(jsonPath("$.included[0].id").value(author.getId().toString()))
                .andExpect(jsonPath("$.included[0].attributes.firstName").value("Frank"))
                .andExpect(jsonPath("$.included[0].attributes.lastName").value("Herbert"))
                // relationship pointer must match included id
                .andExpect(jsonPath("$.data[0].relationships.author.data.id").value(author.getId().toString()));
    }

    @Test
    void getBooks_includeAuthor_deduplicatesSameAuthorAcrossBooks() throws Exception {
        // Two books, same author — included must contain author only once
        makeBook("Dune Messiah", 1969, null, author, new ArrayList<>());

        mockMvc.perform(get(BASE_URL).param("include", "author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.included.length()").value(1));
    }

    @Test
    void getBooks_includeGenres_includedMatchesRelationship() throws Exception {
        mockMvc.perform(get(BASE_URL).param("include", "genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.included").isArray())
                .andExpect(jsonPath("$.included[0].type").value("genres"))
                .andExpect(jsonPath("$.included[0].id").value(sciFiGenre.getId().toString()))
                .andExpect(jsonPath("$.included[0].attributes.name").value("Sci-Fi"))
                .andExpect(jsonPath("$.data[0].relationships.genres.data[0].id")
                        .value(sciFiGenre.getId().toString()));
    }

    @Test
    void getBooks_noInclude_noIncludedField() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.included").doesNotExist());
    }

    // ─── GET /api/v1/books/{id} ─────────────────────────────────────────

    @Test
    void getBookById_returnsExactBook() throws Exception {
        Book foundation = makeBook("Foundation", 1951, "978-0-553-29335-7", null, new ArrayList<>());

        // Request book by book.id — must return Dune, not Foundation
        mockMvc.perform(get(BASE_URL + "/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(book.getId().toString()))
                .andExpect(jsonPath("$.data.attributes.title").value("Dune"))
                .andExpect(jsonPath("$.data.attributes.isbn").value("978-0-441-17271-9"))
                .andExpect(jsonPath("$.data.attributes.publishedYear").value(1965))
                .andExpect(jsonPath("$.data.relationships.author.data.id").value(author.getId().toString()));

        // Request foundation — must return Foundation, not Dune
        mockMvc.perform(get(BASE_URL + "/{id}", foundation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(foundation.getId().toString()))
                .andExpect(jsonPath("$.data.attributes.title").value("Foundation"))
                .andExpect(jsonPath("$.data.attributes.isbn").value("978-0-553-29335-7"))
                .andExpect(jsonPath("$.data.relationships").doesNotExist());
    }

    @Test
    void getBookById_withIncludeAuthor_returnsIncluded() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", book.getId()).param("include", "author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(book.getId().toString()))
                .andExpect(jsonPath("$.included").isArray())
                .andExpect(jsonPath("$.included[0].type").value("authors"))
                .andExpect(jsonPath("$.included[0].id").value(author.getId().toString()));
    }

    @Test
    void getBookById_withSparseFieldsets_returnsOnlyRequestedFields() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", book.getId()).param("fields[books]", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.title").value("Dune"))
                .andExpect(jsonPath("$.data.attributes.isbn").doesNotExist())
                .andExpect(jsonPath("$.data.attributes.publishedYear").doesNotExist());
    }

    @Test
    void getBookById_bookWithoutAuthorAndGenres_hasNoRelationships() throws Exception {
        Book bare = makeBook("Bare Book", 2000, null, null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL + "/{id}", bare.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(bare.getId().toString()))
                .andExpect(jsonPath("$.data.relationships").doesNotExist());
    }

    @Test
    void getBookById_notFound_returns404WithErrorBody() throws Exception {
        mockMvc.perform(get(BASE_URL + "/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status").value("404"))
                .andExpect(jsonPath("$.errors[0].detail").value("Book not found with id: 999999"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    // ─── POST /api/v1/books ─────────────────────────────────────────────

    @Test
    void createBook_returns201WithAllAttributes() throws Exception {
        String body = """
                {
                  "data": {
                    "type": "books",
                    "attributes": {
                      "title": "Neuromancer",
                      "summary": "Cyberpunk classic",
                      "publishedYear": 1984,
                      "isbn": "978-0-441-56956-4"
                    }
                  }
                }
                """;

        mockMvc.perform(post(BASE_URL).contentType(CONTENT_TYPE).content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value("books"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.attributes.title").value("Neuromancer"))
                .andExpect(jsonPath("$.data.attributes.summary").value("Cyberpunk classic"))
                .andExpect(jsonPath("$.data.attributes.publishedYear").value(1984))
                .andExpect(jsonPath("$.data.attributes.isbn").value("978-0-441-56956-4"));
    }

    @Test
    void createBook_persistedToDatabase() throws Exception {
        String body = """
                {
                  "data": {
                    "type": "books",
                    "attributes": {"title": "Persisted Book", "publishedYear": 2000}
                  }
                }
                """;

        String response = mockMvc.perform(post(BASE_URL).contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // extract id from response and verify GET returns it
        String id = com.jayway.jsonpath.JsonPath.read(response, "$.data.id");

        mockMvc.perform(get(BASE_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.title").value("Persisted Book"));
    }

    @Test
    void createBook_withoutJsonApiContentType_returns415() throws Exception {
        String body = """
                {"data": {"type": "books", "attributes": {"title": "Test"}}}
                """;

        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnsupportedMediaType());
    }

    // ─── PATCH /api/v1/books/{id} ───────────────────────────────────────

    @Test
    void updateBook_titleOnly_otherFieldsUnchanged() throws Exception {
        String body = """
                {"data": {"type": "books", "attributes": {"title": "Dune Messiah"}}}
                """;

        mockMvc.perform(patch(BASE_URL + "/{id}", book.getId()).contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(book.getId().toString()))
                .andExpect(jsonPath("$.data.attributes.title").value("Dune Messiah"))
                .andExpect(jsonPath("$.data.attributes.isbn").value("978-0-441-17271-9"))
                .andExpect(jsonPath("$.data.attributes.summary").value("A desert planet story"))
                .andExpect(jsonPath("$.data.attributes.publishedYear").value(1965));
    }

    @Test
    void updateBook_isbnOnly_otherFieldsUnchanged() throws Exception {
        String body = """
                {"data": {"type": "books", "attributes": {"isbn": "000-0-000-00000-0"}}}
                """;

        mockMvc.perform(patch(BASE_URL + "/{id}", book.getId()).contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.isbn").value("000-0-000-00000-0"))
                .andExpect(jsonPath("$.data.attributes.title").value("Dune"))
                .andExpect(jsonPath("$.data.attributes.publishedYear").value(1965));
    }

    @Test
    void updateBook_publishedYearOnly_otherFieldsUnchanged() throws Exception {
        String body = """
                {"data": {"type": "books", "attributes": {"publishedYear": 1969}}}
                """;

        mockMvc.perform(patch(BASE_URL + "/{id}", book.getId()).contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.publishedYear").value(1969))
                .andExpect(jsonPath("$.data.attributes.title").value("Dune"))
                .andExpect(jsonPath("$.data.attributes.isbn").value("978-0-441-17271-9"));
    }

    @Test
    void updateBook_affectsOnlyTargetBook() throws Exception {
        Book foundation = makeBook("Foundation", 1951, "978-0-553-29335-7", null, new ArrayList<>());

        String body = """
                {"data": {"type": "books", "attributes": {"title": "Dune Messiah"}}}
                """;

        mockMvc.perform(patch(BASE_URL + "/{id}", book.getId()).contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isOk());

        // Foundation must be unchanged
        mockMvc.perform(get(BASE_URL + "/{id}", foundation.getId()))
                .andExpect(jsonPath("$.data.attributes.title").value("Foundation"))
                .andExpect(jsonPath("$.data.attributes.isbn").value("978-0-553-29335-7"));
    }

    @Test
    void updateBook_notFound_returns404() throws Exception {
        String body = """
                {"data": {"type": "books", "attributes": {"title": "Ghost"}}}
                """;

        mockMvc.perform(patch(BASE_URL + "/999999").contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].status").value("404"));
    }

    // ─── DELETE /api/v1/books/{id} ──────────────────────────────────────

    @Test
    void deleteBook_returns204_andBookIsGone() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", book.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/{id}", book.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_affectsOnlyTargetBook() throws Exception {
        Book foundation = makeBook("Foundation", 1951, null, null, new ArrayList<>());

        mockMvc.perform(delete(BASE_URL + "/{id}", book.getId()))
                .andExpect(status().isNoContent());

        // Foundation must still exist
        mockMvc.perform(get(BASE_URL + "/{id}", foundation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.title").value("Foundation"));

        // List must contain only Foundation
        mockMvc.perform(get(BASE_URL))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(foundation.getId().toString()));
    }

    // ─── GET /api/v1/books/{id}/author ─────────────────────────────────

    @Test
    void getBookAuthor_returnsCorrectAuthor() throws Exception {
        Author anotherAuthor = new Author();
        anotherAuthor.setFirstName("Isaac");
        anotherAuthor.setLastName("Asimov");
        anotherAuthor.setBirthDate(LocalDate.of(1920, 1, 2));
        anotherAuthor = authorRepository.save(anotherAuthor);

        Book foundation = makeBook("Foundation", 1951, null, anotherAuthor, new ArrayList<>());

        // Dune → Frank Herbert
        mockMvc.perform(get(BASE_URL + "/{id}/author", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(author.getId().toString()))
                .andExpect(jsonPath("$.data.attributes.firstName").value("Frank"));

        // Foundation → Isaac Asimov
        mockMvc.perform(get(BASE_URL + "/{id}/author", foundation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(anotherAuthor.getId().toString()))
                .andExpect(jsonPath("$.data.attributes.firstName").value("Isaac"));
    }

    @Test
    void getBookAuthor_whenNoAuthor_returnsEmptyResponse() throws Exception {
        Book bare = makeBook("Bare Book", 2000, null, null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL + "/{id}/author", bare.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    // ─── GET /api/v1/books/{id}/genres ─────────────────────────────────

    @Test
    void getBookGenres_returnsCorrectGenres() throws Exception {
        // book has sciFiGenre; create another book with fantasyGenre
        Book fantasyBook = makeBook("Conan", 1932, null, null, new ArrayList<>(List.of(fantasyGenre)));

        mockMvc.perform(get(BASE_URL + "/{id}/genres", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(sciFiGenre.getId().toString()))
                .andExpect(jsonPath("$.data[0].attributes.name").value("Sci-Fi"));

        mockMvc.perform(get(BASE_URL + "/{id}/genres", fantasyBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(fantasyGenre.getId().toString()))
                .andExpect(jsonPath("$.data[0].attributes.name").value("Fantasy"));
    }

    @Test
    void getBookGenres_whenNoGenres_returnsEmptyArray() throws Exception {
        Book bare = makeBook("Bare Book", 2000, null, null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL + "/{id}/genres", bare.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ─── GET /api/v1/books/{id}/relationships/author ────────────────────

    @Test
    void getAuthorRelationship_returnsCorrectIdentifier() throws Exception {
        Author anotherAuthor = new Author();
        anotherAuthor.setFirstName("Isaac");
        anotherAuthor.setLastName("Asimov");
        anotherAuthor.setBirthDate(LocalDate.of(1920, 1, 2));
        anotherAuthor = authorRepository.save(anotherAuthor);

        Book foundation = makeBook("Foundation", 1951, null, anotherAuthor, new ArrayList<>());

        mockMvc.perform(get(BASE_URL + "/{id}/relationships/author", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("authors"))
                .andExpect(jsonPath("$.data.id").value(author.getId().toString()));

        // Foundation's relationship must point to Asimov, not Herbert
        mockMvc.perform(get(BASE_URL + "/{id}/relationships/author", foundation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(anotherAuthor.getId().toString()));
    }

    @Test
    void getAuthorRelationship_whenNoAuthor_returnsEmptyResponse() throws Exception {
        Book bare = makeBook("Bare Book", 2000, null, null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL + "/{id}/relationships/author", bare.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    // ─── PATCH /api/v1/books/{id}/relationships/author ──────────────────

    @Test
    void updateAuthorRelationship_setsNewAuthor_verifiedByGet() throws Exception {
        Author asimov = new Author();
        asimov.setFirstName("Isaac");
        asimov.setLastName("Asimov");
        asimov.setBirthDate(LocalDate.of(1920, 1, 2));
        asimov = authorRepository.save(asimov);

        String body = String.format("""
                {"data": {"type": "author", "id": "%s"}}
                """, asimov.getId());

        mockMvc.perform(patch(BASE_URL + "/{id}/relationships/author", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isOk());

        // Verify via relationship endpoint
        mockMvc.perform(get(BASE_URL + "/{id}/relationships/author", book.getId()))
                .andExpect(jsonPath("$.data.id").value(asimov.getId().toString()));

        // Verify via related endpoint
        mockMvc.perform(get(BASE_URL + "/{id}/author", book.getId()))
                .andExpect(jsonPath("$.data.attributes.firstName").value("Isaac"));
    }

    @Test
    void updateAuthorRelationship_wrongType_returns400() throws Exception {
        String body = """
                {"data": {"type": "authors", "id": "1"}}
                """;

        mockMvc.perform(patch(BASE_URL + "/{id}/relationships/author", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].status").value("400"))
                .andExpect(jsonPath("$.errors[0].detail").value("Not correct author type"));
    }

    @Test
    void updateAuthorRelationship_nullData_returns400() throws Exception {
        String body = """
                {"data": null}
                """;

        mockMvc.perform(patch(BASE_URL + "/{id}/relationships/author", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].status").value("400"));
    }

    // ─── GET /api/v1/books/{id}/relationships/genres ────────────────────

    @Test
    void getGenresRelationship_returnsCorrectIdentifiers() throws Exception {
        // book has sciFiGenre; add fantasyGenre too
        book.getGenres().add(fantasyGenre);
        bookRepository.save(book);

        mockMvc.perform(get(BASE_URL + "/{id}/relationships/genres", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[?(@.id == '" + sciFiGenre.getId() + "')].type").value("genres"))
                .andExpect(jsonPath("$.data[?(@.id == '" + fantasyGenre.getId() + "')].type").value("genres"));
    }

    @Test
    void getGenresRelationship_whenNoGenres_returnsEmptyArray() throws Exception {
        Book bare = makeBook("Bare Book", 2000, null, null, new ArrayList<>());

        mockMvc.perform(get(BASE_URL + "/{id}/relationships/genres", bare.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ─── POST /api/v1/books/{id}/relationships/genres ───────────────────

    @Test
    void addGenres_newGenreAdded_previousGenreStillPresent() throws Exception {
        String body = String.format("""
                {"data": [{"type": "genres", "id": "%s"}]}
                """, fantasyGenre.getId());

        mockMvc.perform(post(BASE_URL + "/{id}/relationships/genres", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/{id}/relationships/genres", book.getId()))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[?(@.id == '" + sciFiGenre.getId() + "')]").exists())
                .andExpect(jsonPath("$.data[?(@.id == '" + fantasyGenre.getId() + "')]").exists());
    }

    @Test
    void addGenres_existingGenreSentAgain_noDuplicateCreated() throws Exception {
        String body = String.format("""
                {"data": [{"type": "genres", "id": "%s"}]}
                """, sciFiGenre.getId());

        mockMvc.perform(post(BASE_URL + "/{id}/relationships/genres", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/{id}/relationships/genres", book.getId()))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(sciFiGenre.getId().toString()));
    }

    @Test
    void addGenres_affectsOnlyTargetBook() throws Exception {
        Book anotherBook = makeBook("Foundation", 1951, null, null, new ArrayList<>());

        String body = String.format("""
                {"data": [{"type": "genres", "id": "%s"}]}
                """, fantasyGenre.getId());

        mockMvc.perform(post(BASE_URL + "/{id}/relationships/genres", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isNoContent());

        // anotherBook must still have no genres
        mockMvc.perform(get(BASE_URL + "/{id}/relationships/genres", anotherBook.getId()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ─── PATCH /api/v1/books/{id}/relationships/genres ──────────────────

    @Test
    void replaceGenres_oldGoneNewPresent() throws Exception {
        String body = String.format("""
                {"data": [{"type": "genres", "id": "%s"}]}
                """, fantasyGenre.getId());

        mockMvc.perform(patch(BASE_URL + "/{id}/relationships/genres", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/{id}/relationships/genres", book.getId()))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(fantasyGenre.getId().toString()))
                .andExpect(jsonPath("$.data[?(@.id == '" + sciFiGenre.getId() + "')]").doesNotExist());
    }

    @Test
    void replaceGenres_withEmptyList_removesAll() throws Exception {
        String body = """
                {"data": []}
                """;

        mockMvc.perform(patch(BASE_URL + "/{id}/relationships/genres", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/{id}/relationships/genres", book.getId()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ─── DELETE /api/v1/books/{id}/relationships/genres ─────────────────

    @Test
    void removeGenres_removesOnlyTargetGenre_otherRemains() throws Exception {
        // Give book both genres first
        book.getGenres().add(fantasyGenre);
        bookRepository.save(book);

        // Remove only sciFiGenre
        String body = String.format("""
                {"data": [{"type": "genres", "id": "%s"}]}
                """, sciFiGenre.getId());

        mockMvc.perform(delete(BASE_URL + "/{id}/relationships/genres", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/{id}/relationships/genres", book.getId()))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(fantasyGenre.getId().toString()))
                .andExpect(jsonPath("$.data[?(@.id == '" + sciFiGenre.getId() + "')]").doesNotExist());
    }

    @Test
    void removeGenres_removesMultipleAtOnce() throws Exception {
        book.getGenres().add(fantasyGenre);
        bookRepository.save(book);

        String body = String.format("""
                {
                  "data": [
                    {"type": "genres", "id": "%s"},
                    {"type": "genres", "id": "%s"}
                  ]
                }
                """, sciFiGenre.getId(), fantasyGenre.getId());

        mockMvc.perform(delete(BASE_URL + "/{id}/relationships/genres", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/{id}/relationships/genres", book.getId()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void removeGenres_affectsOnlyTargetBook() throws Exception {
        Book anotherBook = makeBook("Foundation", 1951, null, null, new ArrayList<>(List.of(sciFiGenre)));

        String body = String.format("""
                {"data": [{"type": "genres", "id": "%s"}]}
                """, sciFiGenre.getId());

        // Remove sciFiGenre from book (Dune)
        mockMvc.perform(delete(BASE_URL + "/{id}/relationships/genres", book.getId())
                        .contentType(CONTENT_TYPE).content(body))
                .andExpect(status().isNoContent());

        // anotherBook must still have sciFiGenre
        mockMvc.perform(get(BASE_URL + "/{id}/relationships/genres", anotherBook.getId()))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(sciFiGenre.getId().toString()));
    }

    // ─── helper ─────────────────────────────────────────────────────────

    private Book makeBook(String title, int year, String isbn, Author bookAuthor, List<Genre> genres) {
        Book b = new Book();
        b.setTitle(title);
        b.setPublishedYear(year);
        b.setIsbn(isbn);
        b.setAuthor(bookAuthor);
        b.setGenres(genres);
        return bookRepository.save(b);
    }
}