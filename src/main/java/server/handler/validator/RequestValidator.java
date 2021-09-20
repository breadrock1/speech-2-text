package server.handler.validator;

import java.lang.reflect.Field;
import java.util.List;

public class RequestValidator {

    public void validate(Object request) throws InvalidRequestException {
        for (Field field : request.getClass().getDeclaredFields()) {
            Required required = field.getAnnotation(Required.class);
            if (required != null) {
                checkField(request, field, required.type());
            }
            if (List.class.isAssignableFrom(field.getType())) {
                checkList(request, field);
            }
        }
    }

    private void checkList(Object request, Field field) throws InvalidRequestException {
        List<?> list;
        try {
            list = (List<?>) field.get(request);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        for (Object item : list) {
            validate(item);
        }
    }

    private void checkField(Object request, Field field, RequirementType type) throws InvalidRequestException {
        Object value;
        try {
            value = field.get(request);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        switch (type) {
            case NOT_NULL: {
                if (value == null) {
                    throw InvalidRequestException.fieldIsRequired(field.getName());
                }
            }
            case NOT_NULL_NOT_EMPTY: {
                if (value == null) {
                    throw InvalidRequestException.fieldIsRequired(field.getName());
                }
                if (value instanceof List) {
                    if (((List<?>) value).isEmpty()) {
                        throw InvalidRequestException.fieldIsEmpty(field.getName());
                    }
                }
                if (value instanceof String) {
                    if (((String) value).isEmpty()) {
                        throw InvalidRequestException.fieldIsEmpty(field.getName());
                    }
                }
            }
        }
    }

}
