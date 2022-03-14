package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.repository.LogRepository;
import com.project.mentoridge.modules.log.vo.Log;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.project.mentoridge.modules.log.vo.Log.*;

@Slf4j
@RequiredArgsConstructor
public abstract class LogService<T> {

    @Getter
    protected static class Property {

        public Property(String field, String name) {
            this.field = field;
            this.name = name;
        }

        private String field;
        private String name;
    }
    protected List<Property> properties = new ArrayList<>();

    private final LogRepository logRepository;

    // 사용자 활동 이력
    // TODO - MySQL? MongoDB?
    // TODO - PrintStream, PrintWriter
/*
    public void select(User user, T vo) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            this.select(pw, user, vo);

        } catch(Exception e) {
            log.error("log-error : [select] user : {}, vo : {}", user.getUsername(), vo.toString());
        }
    }
    protected abstract void select(PrintWriter pw, User user, T vo);*/

    public void insert(User user, T vo) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            this.insert(pw, vo);

            logRepository.save(buildInsertLog(user.getUsername(), sw.toString()));

        } catch(Exception e) {
            log.error("log-error : [insert] user : {}, vo : {}", user.getUsername(), vo.toString());
            e.printStackTrace();
        }
    }
    protected abstract void insert(PrintWriter pw, T vo) throws NoSuchFieldException, IllegalAccessException;

    protected void printInsertLogContent(PrintWriter pw, T vo, List<Property> properties) throws NoSuchFieldException, IllegalAccessException {

        Object value;
        int count = 0;
        for(Property property : properties) {
            Field field = vo.getClass().getDeclaredField(property.getField());
            field.setAccessible(true);
            value = field.get(vo);
            if (ObjectUtils.isNotEmpty(value)) {
                if (count == 0) {
                    pw.print(String.format("%s : %s", property.getName(), value.toString()));
                } else {
                    pw.print(String.format(", %s : %s", property.getName(), value.toString()));
                }
                count += 1;
            }
        }
    }

    // 차이점만 기록
    public void update(User user, T before, T after) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            this.update(pw, before, after);
            logRepository.save(buildUpdateLog(user.getUsername(), sw.toString()));

        } catch(Exception e) {
            log.error("log-error : [update] user : {}, vo : {} -> {}", user.getUsername(), before.toString(), after.toString());
            e.printStackTrace();
        }
    }
    protected abstract void update(PrintWriter pw, T before, T after) throws NoSuchFieldException, IllegalAccessException;

    protected void printUpdateLogContent(PrintWriter pw, T before, T after, List<Property> properties) throws NoSuchFieldException, IllegalAccessException {

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
                        pw.print(String.format("%s : %s → %s", property.getName(), beforeStr, afterStr));
                    } else {
                        pw.print(String.format(", %s : %s → %s", property.getName(), beforeStr, afterStr));
                    }
                    count += 1;
                }
            }
        }
    }

    public void delete(User user, T vo) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            this.delete(pw, vo);
            logRepository.save(buildDeleteLog(user.getUsername(), sw.toString()));

        } catch(Exception e) {
            log.error("log-error : [delete] user : {}, vo : {}", user.getUsername(), vo.toString());
            e.printStackTrace();
        }
    }
    protected abstract void delete(PrintWriter pw, T vo) throws NoSuchFieldException, IllegalAccessException;

    protected void printDeleteLogContent(PrintWriter pw, T vo, List<Property> properties) throws NoSuchFieldException, IllegalAccessException {

        Object value;
        int count = 0;
        for(Property property : properties) {
            Field field = vo.getClass().getDeclaredField(property.getField());
            field.setAccessible(true);
            value = field.get(vo);
            if (ObjectUtils.isNotEmpty(value)) {
                if (count == 0) {
                    pw.print(String.format("%s : %s", property.getName(), value.toString()));
                } else {
                    pw.print(String.format(", %s : %s", property.getName(), value.toString()));
                }
                count += 1;
            }
        }
    }
}
