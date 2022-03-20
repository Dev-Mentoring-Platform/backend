package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.log.component.LogService.Property;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class MappingItemsLogService<T> {

    protected List<Property> properties = new ArrayList<>();

    protected void getLogContent(StringBuilder sb, T vo) throws NoSuchFieldException, IllegalAccessException {

        Object value;
        int count = 0;
        for(Property property : properties) {
            Field field = vo.getClass().getDeclaredField(property.getField());
            field.setAccessible(true);
            value = field.get(vo);
            if (ObjectUtils.isNotEmpty(value)) {
                if (count == 0) {
                    sb.append(String.format("%s : %s", property.getName(), value.toString()));
                } else {
                    sb.append(String.format(", %s : %s", property.getName(), value.toString()));
                }
                count += 1;
            }
        }
    }

    // TODO
/*
    protected void getUpdateLogContent(StringBuilder sb, T before, T after) throws NoSuchFieldException, IllegalAccessException {

        Object beforeValue;
        Object afterValue;
        int count = 0;
        for(Property property : properties) {

            Field beforeField = before.getClass().getDeclaredField(property.getField());
            beforeField.setAccessible(true);
            beforeValue = beforeField.get(before);

            Field afterField = after.getClass().getDeclaredField(property.getField());
            afterField.setAccessible(true);
            afterValue = afterField.get(after);

            if (ObjectUtils.isNotEmpty(beforeValue) || ObjectUtils.isNotEmpty(afterValue)) {
                String beforeStr = beforeValue.toString();
                String afterStr = afterValue.toString();
                if (!beforeStr.equals(afterStr)) {
                    if (count == 0) {
                        sb.append(String.format("%s : %s → %s", property.getName(), beforeStr, afterStr));
                    } else {
                        sb.append(String.format(", %s : %s → %s", property.getName(), beforeStr, afterStr));
                    }
                    count += 1;
                }
            }
        }
    }*/
}
