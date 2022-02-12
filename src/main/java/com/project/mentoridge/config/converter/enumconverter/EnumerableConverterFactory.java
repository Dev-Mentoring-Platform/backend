package com.project.mentoridge.config.converter.enumconverter;

import com.project.mentoridge.modules.base.Enumerable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class EnumerableConverterFactory implements ConverterFactory<String, Enum<? extends Enumerable>> {

    public EnumerableConverterFactory() {}

    @Override
    public <T extends Enum<? extends Enumerable>> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumerableConverter(targetType);
    }

    private static class StringToEnumerableConverter<T extends Enum<? extends Enumerable>> implements Converter<String, T> {
        private final Class<T> clazz;

        public StringToEnumerableConverter(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T convert(String source) {
            if (StringUtils.isBlank(source)) {
                return null;
            }
            Enumerable[] enumConstants = (Enumerable[]) this.clazz.getEnumConstants();
            return (T) Enumerable.findToNull(source, enumConstants);
        }
    }
}
