package com.project.mentoridge.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Array;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiValueConverter {

    public static MultiValueMap<String, String> convert(Object request) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> param = objectMapper.convertValue(request, new TypeReference<Map<String, Object>>() {
        }).entrySet().stream().map((entry) -> {
            Object entryValue = entry.getValue();
            if (Objects.isNull(entryValue)) {
                return new SimpleEntry<String, String>(entry.getKey(), "");
            }
            Stream<?> stream = null;
            if (entryValue instanceof List) {
                stream = ((List<?>) entryValue).stream();
            } else if (entryValue instanceof Array) {
                stream = Stream.of(entryValue);
            }

            String value = Optional.ofNullable(stream).map(s -> {
                return s.map(v -> {
                    return Optional.ofNullable(v).map(String::valueOf).orElse("");
                }).collect(Collectors.joining(","));
            }).orElse(Objects.isNull(entryValue) ? null : String.valueOf(entryValue));
            return new SimpleEntry<String, String>(entry.getKey(), value);
        }).collect(Collectors.collectingAndThen(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue), Collections::unmodifiableMap));
        map.setAll(param);
        return map;
    }

}
