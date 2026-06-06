package kz.app.jsonapidemo.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookData {

    private String title;
    private String summary;
    private Integer publishedYear;
    private String isbn;
}
