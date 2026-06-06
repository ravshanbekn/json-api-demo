package kz.app.jsonapidemo.service;

import kz.app.jsonapidemo.exception.ObjectNotFoundException;
import kz.app.jsonapidemo.model.data.AuthorData;
import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Author not found with id: " + id));
    }

    @Transactional
    public Author create(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public Author update(Long id, AuthorData authorData) {
        Author author = findById(id);
        if (authorData.getFirstName() != null) author.setFirstName(authorData.getFirstName());
        if (authorData.getLastName() != null) author.setLastName(authorData.getLastName());
        if (authorData.getBio() != null) author.setBio(authorData.getBio());
        return authorRepository.save(author);
    }

    @Transactional
    public void delete(Long id) {
        Author author = findById(id);
        authorRepository.delete(author);
    }
}
