package kz.app.jsonapidemo.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import kz.app.jsonapidemo.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSpecification implements Specification<Book> {

    private Map<String, String> filters;

    @Override
    public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (filters == null || filters.isEmpty()) {
            return cb.conjunction();
        }

        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            predicates.add(
                cb.like(cb.lower(root.get(entry.getKey())), entry.getValue().toLowerCase() + "%")
            );
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
