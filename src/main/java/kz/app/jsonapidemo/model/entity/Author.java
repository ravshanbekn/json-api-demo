package kz.app.jsonapidemo.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kz.app.jsonapidemo.model.common.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "author")
@NoArgsConstructor
@AllArgsConstructor
public class Author extends AbstractEntity {

    private String firstName;

    private String lastName;

    private String bio;

    private LocalDate birthDate;

    @OneToMany(mappedBy = "author")
    private List<Book> books;
}
