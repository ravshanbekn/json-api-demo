package kz.app.jsonapidemo.model.jsonapi;

import lombok.Data;

@Data
public class ErrorObject {

    private String status;
    private String title;
    private String detail;
}
