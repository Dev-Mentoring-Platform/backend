package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.repository.LogRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.project.mentoridge.modules.log.vo.Log.*;

@Slf4j
@RequiredArgsConstructor
public abstract class LogService<T> {

    @Getter
    public static class Property {

        public Property(String field, String name) {
            this.field = field;
            this.name = name;
        }

        private String field;
        private String name;
    }

    protected List<Property> properties = new ArrayList<>();
    protected Map<String, Function<T, String>> functions = new HashMap<>();
    protected String title;

    // private static final String SYSTEM = "system";
    private static final String ADMIN = "admin";
    protected final LogRepository logRepository;

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

    public String insert(User user, T vo) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.print(title);
            this.insert(pw, vo);

            String log = sw.toString();
            logRepository.saveLog(buildInsertLog(user.getUsername(), log));

            return log;

        } catch(Exception e) {
            log.error("log-error : [insert] user : {}, vo : {}", user.getUsername(), vo.toString());
            e.printStackTrace();
        }
        return null;
    }
    protected abstract void insert(PrintWriter pw, T vo) throws NoSuchFieldException, IllegalAccessException;

        protected void printInsertLogContent(PrintWriter pw, T vo, List<Property> properties, Map<String, Function<T, String>> functions) throws NoSuchFieldException, IllegalAccessException {

            Object value;
            String _value;
            int count = 0;
            for(Property property : properties) {

                String _field = property.getField();
                if (functions.containsKey(_field)) {

                    Function<T, String> func = functions.get(_field);
                    _value = func.apply(vo);
                    if (count == 0) {
                        pw.print(String.format("%s : %s", property.getName(), _value));
                    } else {
                        pw.print(String.format(", %s : %s", property.getName(), _value));
                    }

                } else {

                    Field field = vo.getClass().getDeclaredField(_field);
                    field.setAccessible(true);
                    value = field.get(vo);
                    if (ObjectUtils.isNotEmpty(value)) {
                        if (count == 0) {
                            pw.print(String.format("%s : %s", property.getName(), value.toString()));
                        } else {
                            pw.print(String.format(", %s : %s", property.getName(), value.toString()));
                        }
                    }
                }
                count += 1;
            }
        }

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
    public String update(User user, T before, T after) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.print(title);
            this.update(pw, before, after);

            String log = sw.toString();
            logRepository.saveLog(buildUpdateLog(user.getUsername(), log));

            return log;

        } catch(Exception e) {
            log.error("log-error : [update] user : {}, vo : {} -> {}", user.getUsername(), before.toString(), after.toString());
            e.printStackTrace();
        }
        return null;
    }
    protected abstract void update(PrintWriter pw, T before, T after) throws NoSuchFieldException, IllegalAccessException;

        protected void printUpdateLogContent(PrintWriter pw, T before, T after, List<Property> properties, Map<String, Function<T, String>> functions) throws NoSuchFieldException, IllegalAccessException {

            Object beforeValue;
            Object afterValue;
            String _beforeValue;
            String _afterValue;
            int count = 0;
            for(Property property : properties) {

                String _field = property.getField();
                if (functions.containsKey(_field)) {

                    Function<T, String> func = functions.get(_field);
                    _beforeValue = func.apply(before);
                    _afterValue = func.apply(after);
                    if (!_beforeValue.equals(_afterValue)) {
                        if (count == 0) {
                            pw.print(String.format("%s : %s → %s", property.getName(), _beforeValue, _afterValue));
                        } else {
                            pw.print(String.format(", %s : %s → %s", property.getName(), _beforeValue, _afterValue));
                        }
                        count += 1;
                    }

                } else {

                    Field beforeField = before.getClass().getDeclaredField(property.getField());
                    beforeField.setAccessible(true);
                    beforeValue = beforeField.get(before);

                    Field afterField = after.getClass().getDeclaredField(property.getField());
                    afterField.setAccessible(true);
                    afterValue = afterField.get(after);

                    if (ObjectUtils.isNotEmpty(beforeValue) || ObjectUtils.isNotEmpty(afterValue)) {
                        String beforeStr = beforeValue != null ? beforeValue.toString() : "없음";
                        String afterStr = afterValue != null ? afterValue.toString() : "없음";
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
        }

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

    public String delete(User user, T vo) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.print(title);
            this.delete(pw, vo);

            String log = sw.toString();
            logRepository.saveLog(buildDeleteLog(user.getUsername(), log));

            return log;

        } catch(Exception e) {
            log.error("log-error : [delete] user : {}, vo : {}", user.getUsername(), vo.toString());
            e.printStackTrace();
        }
        return null;
    }
    protected abstract void delete(PrintWriter pw, T vo) throws NoSuchFieldException, IllegalAccessException;

        protected void printDeleteLogContent(PrintWriter pw, T vo, List<Property> properties, Map<String, Function<T, String>> functions) throws NoSuchFieldException, IllegalAccessException {

            Object value;
            String _value;
            int count = 0;
            for(Property property : properties) {

                String _field = property.getField();
                if (functions.containsKey(_field)) {

                    Function<T, String> func = functions.get(_field);
                    _value = func.apply(vo);
                    if (count == 0) {
                        pw.print(String.format("%s : %s", property.getName(), _value));
                    } else {
                        pw.print(String.format(", %s : %s", property.getName(), _value));
                    }

                } else {

                    Field field = vo.getClass().getDeclaredField(_field);
                    field.setAccessible(true);
                    value = field.get(vo);
                    if (ObjectUtils.isNotEmpty(value)) {
                        if (count == 0) {
                            pw.print(String.format("%s : %s", property.getName(), value.toString()));
                        } else {
                            pw.print(String.format(", %s : %s", property.getName(), value.toString()));
                        }
                    }
                }
                count += 1;
            }
        }

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

    protected void updateStatus(User user, T after, String propertyField, String propertyName) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.print(title);
            this.printUpdateStatusLogContent(pw, after, new Property(propertyField, propertyName));
            logRepository.saveLog(buildUpdateLog(user.getUsername(), sw.toString()));

        } catch(Exception e) {
            log.error("log-error : [update-status] user : {}, vo : {}", user.getUsername(), after.toString());
            e.printStackTrace();
        }
    }

    protected void updateStatus(User user, T before, T after, String propertyField, String propertyName) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.print(title);
            this.printUpdateStatusLogContent(pw, before, after, new Property(propertyField, propertyName));
            logRepository.saveLog(buildUpdateLog(user.getUsername(), sw.toString()));

        } catch(Exception e) {
            log.error("log-error : [update-status] user : {}, vo : {} -> {}", user.getUsername(), before.toString(), after.toString());
            e.printStackTrace();
        }
    }

    protected void updateStatusByAdmin(T before, T after, String propertyField, String propertyName) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.print(title);
            this.printUpdateStatusLogContent(pw, before, after, new Property(propertyField, propertyName));
            logRepository.saveLog(buildUpdateLog(ADMIN, sw.toString()));

        } catch(Exception e) {
            log.error("log-error : [update-status] user : {}, vo : {} -> {}", ADMIN, before.toString(), after.toString());
            e.printStackTrace();
        }
    }

    protected String updateStatusByAdmin(T after, String propertyField, String propertyName) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.print(title);
            this.printUpdateStatusLogContent(pw, after, new Property(propertyField, propertyName));

            String log = sw.toString();
            logRepository.saveLog(buildUpdateLog(ADMIN, log));

            return log;

        } catch(Exception e) {
            log.error("log-error : [update-status] user : {}, vo : {}", ADMIN, after.toString());
            e.printStackTrace();
        }
        return null;
    }

        private void printUpdateStatusLogContent(PrintWriter pw, T after, Property property) throws NoSuchFieldException, IllegalAccessException {

            Field afterField = after.getClass().getDeclaredField(property.getField());
            afterField.setAccessible(true);
            Object afterValue = afterField.get(after);

            if (afterValue instanceof Boolean) {
                Boolean value = (Boolean) afterValue;
                pw.print(String.format("%s : %s → %s", property.getName(), !value, value));
            }
        }

        private void printUpdateStatusLogContent(PrintWriter pw, T before, T after, Property property) throws NoSuchFieldException, IllegalAccessException {

            Object beforeValue;
            Object afterValue;
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
                    pw.print(String.format("%s : %s → %s", property.getName(), beforeStr, afterStr));
                }
            }

        }
}
