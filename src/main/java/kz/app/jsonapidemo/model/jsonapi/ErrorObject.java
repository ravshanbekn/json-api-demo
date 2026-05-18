package kz.app.jsonapidemo.model.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorObject {

    private String status;
    private String title;
    private String detail;
}
