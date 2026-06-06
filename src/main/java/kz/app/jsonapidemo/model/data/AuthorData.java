package kz.app.jsonapidemo.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorData {

    private String firstName;
    private String lastName;
    private String bio;
}
