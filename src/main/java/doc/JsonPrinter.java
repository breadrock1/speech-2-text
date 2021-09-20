package doc;

import doc.annotation.Description;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class JsonPrinter {
    void print(Class<?> jsonClass, Html html) {
        List<Class<?>> dependencies = new ArrayList<>();
        dependencies.add(jsonClass);
        boolean skipBr = true;
        while (!dependencies.isEmpty()) {
            if (skipBr) {
                skipBr = false;
            } else {
                html.br();
            }
            Class<?> clazz = dependencies.remove(0);
            html.text(clazz.getSimpleName());
            html.startTable().th("Имя").th("Тип").th("Обязательный").th("Описание");
            printObject(clazz, html, dependencies);
            html.endTable();
        }
    }

    private void printObject(Class<?> clazz, Html html, List<Class<?>> dependencies) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            Description description = field.getAnnotation(Description.class);
            html.startTr()
                    .td(field.getName())
                    .td(getType(field, dependencies))
                    .td(isRequired(field))
                    .td(description == null ? "" : description.value())
                    .endTr();
        }
    }

    private String getType(Field field, List<Class<?>> dependencies) {
        Class<?> type = field.getType();
        if (List.class.isAssignableFrom(type)) {
            ParameterizedType itemType = (ParameterizedType) field.getGenericType();
            return getTypeName((Class<?>) itemType.getActualTypeArguments()[0], dependencies) + "[]";
        } else {
            return getTypeName(type, dependencies);
        }
    }

    private String getTypeName(Class<?> type, List<Class<?>> dependencies) {
        if (type == String.class) {
            return "string";
        }
        if (type == boolean.class || type == Boolean.class) {
            return "boolean";
        }
        if (type == int.class || type == Integer.class || type == long.class || type == Long.class) {
            return "integer";
        }
        if (type == float.class || type == Float.class || type == double.class || type == Double.class) {
            return "float";
        }
        if (type.isEnum()) {
            return getEnumType(type);
        }
        dependencies.add(type);
        return type.getSimpleName();
    }

    private static String isRequired(Field field) {
        boolean optional = field.getAnnotation(Nullable.class) != null;
        return optional ? "Нет" : "Да";
    }

    private static String getEnumType(Class<?> type) {
        StringBuilder result = new StringBuilder();
        String delimiter = "";
        for (Object constant : type.getEnumConstants()) {
            result.append(delimiter).append(constant.toString());
            delimiter = " | ";
        }
        return result.toString();
    }
}
