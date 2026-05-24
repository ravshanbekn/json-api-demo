package kz.app.jsonapidemo.service;

import kz.app.jsonapidemo.exception.ObjectNotFoundException;
import kz.app.jsonapidemo.model.data.GenreData;
import kz.app.jsonapidemo.model.entity.Genre;
import kz.app.jsonapidemo.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public Genre findById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Genre not found with id: " + id));
    }

    @Transactional
    public Genre create(Genre genre) {
        return genreRepository.save(genre);
    }

    @Transactional
    public Genre update(Long id, GenreData data) {
        Genre genre = findById(id);
        if (data.getName() != null) genre.setName(data.getName());
        if (data.getDescription() != null) genre.setDescription(data.getDescription());
        return genreRepository.save(genre);
    }

    @Transactional
    public void delete(Long id) {
        Genre genre = findById(id);
        genreRepository.delete(genre);
    }
}
