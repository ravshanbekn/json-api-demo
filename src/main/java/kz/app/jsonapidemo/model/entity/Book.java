package kz.app.jsonapidemo.model.entity;

import jakarta.persistence.*;
import kz.app.jsonapidemo.model.common.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "book")
@NoArgsConstructor
@AllArgsConstructor
public class Book extends AbstractEntity {

    private String title;

    private String summary;

    private Integer publishedYear;

    private String isbn;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToMany
    @JoinTable(name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres;
}
