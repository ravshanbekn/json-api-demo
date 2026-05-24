package kz.app.jsonapidemo.model.jsonapi;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ResourceIdentifierList {

    private List<ResourceIdentifier> data;
}
