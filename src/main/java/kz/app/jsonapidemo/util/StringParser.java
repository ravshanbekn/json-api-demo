package kz.app.jsonapidemo.util;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StringParser {

    public static Map<String, String> getFilters(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return Map.of();
        }

        return params.entrySet().stream()
                .filter(e -> e.getKey().startsWith("filter[") && e.getKey().endsWith("]"))
                .collect(Collectors.toMap(
                        e -> e.getKey().substring(7, e.getKey().length() - 1),
                        Map.Entry::getValue
                ));
    }

    public static Map<String, Set<String>> parse(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return Map.of();
        }
        return params.entrySet().stream()
                .filter(e -> e.getKey().startsWith("fields[") && e.getKey().endsWith("]"))
                .collect(Collectors.toMap(
                        e -> e.getKey().substring(7, e.getKey().length() - 1),
                        e -> Set.of(e.getValue().split(","))
                ));
    }
}
