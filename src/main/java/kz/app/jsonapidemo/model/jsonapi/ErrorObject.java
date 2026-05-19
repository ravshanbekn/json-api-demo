package kz.app.jsonapidemo.model.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class ErrorObject {

    private String id;
    private String status;
    private String code;
    private ErrorSource source;
    private Map<String, Object> meta;
    private String title;
    private String detail;
}
