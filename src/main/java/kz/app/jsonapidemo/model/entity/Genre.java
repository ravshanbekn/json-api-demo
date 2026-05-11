package kz.app.jsonapidemo.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import kz.app.jsonapidemo.model.common.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "genre")
@NoArgsConstructor
@AllArgsConstructor
public class Genre extends AbstractEntity {

    private String name;

    private String description;

    @ManyToMany(mappedBy = "genres")
    private List<Book> books;
}
