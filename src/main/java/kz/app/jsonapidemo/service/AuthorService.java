package kz.app.jsonapidemo.service;

import kz.app.jsonapidemo.mapper.AuthorMapper;
import kz.app.jsonapidemo.model.entity.Author;
import kz.app.jsonapidemo.model.jsonapi.ResourceObject;
import kz.app.jsonapidemo.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorMapper authorMapper;
    private final AuthorRepository authorRepository;

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
    }

    public Author create(ResourceObject resource) {
        Author entity = authorMapper.toEntity(resource);
        return authorRepository.save(entity);
    }
}
