package kz.app.jsonapidemo.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StringParser {

    public static Sort getSort(Map<String, String> params) {
        if (params == null || !params.containsKey("sort")) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (String field : params.get("sort").split(",")) {
            if (field.startsWith("-")) {
                orders.add(Sort.Order.desc(field.substring(1)));
            } else {
                orders.add(Sort.Order.asc(field));
            }
        }
        return Sort.by(orders);
    }

    public static Pageable getPageable(Map<String, String> params) {
        int number = 0;
        int size = 20;
        if (params != null) {
            if (params.containsKey("page[number]")) number = Integer.parseInt(params.get("page[number]"));
            if (params.containsKey("page[size]")) size = Integer.parseInt(params.get("page[size]"));
        }
        return PageRequest.of(number, size, getSort(params));
    }

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
