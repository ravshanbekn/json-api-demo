package kz.app.jsonapidemo.service;

import kz.app.jsonapidemo.exception.ObjectNotFoundException;
import kz.app.jsonapidemo.model.data.BookData;
import kz.app.jsonapidemo.model.entity.Book;
import kz.app.jsonapidemo.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

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
    public void delete(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }
}
