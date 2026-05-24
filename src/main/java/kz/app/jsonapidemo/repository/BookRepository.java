package kz.app.jsonapidemo.repository;

import kz.app.jsonapidemo.model.entity.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"genres"})
    Optional<Book> findWithGenresById(Long id);

    List<Book> findAllByGenres_Id(Long genreId);
}
