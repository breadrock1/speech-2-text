package doc;

import doc.annotation.Description;
import doc.annotation.Responses;
import server.user.auth.Unauthorized;
import server.http.ResponseData;
import server.http.annotation.Body;
import server.http.annotation.Query;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class DocumentationItem {

    private final String httpMethod;
    private final String path;
    private final Method method;

    @Nullable
    private List<DocumentationQueryParameter> queryParameters;

    DocumentationItem(String httpMethod, String path, Method method) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.method = method;
    }

    String getDescription() {
        Description description = method.getAnnotation(Description.class);
        return (description == null) ? method.getName() : description.value();
    }

    boolean isAuthorized() {
        return method.getAnnotation(Unauthorized.class) == null;
    }

    String getHttpMethod() {
        return httpMethod;
    }

    String getPath() {
        return path;
    }

    List<DocumentationQueryParameter> getQueryParameters() {
        if (queryParameters == null) {
            queryParameters = new ArrayList<>(method.getParameterCount());
            for (int i = 0; i < method.getParameterCount(); i++) {
                Query query = method.getParameters()[i].getAnnotation(Query.class);
                if (query == null) continue;
                Description description = method.getParameters()[i].getAnnotation(Description.class);
                queryParameters.add(DocumentationQueryParameter.builder()
                        .name(query.value())
                        .type(method.getParameterTypes()[i])
                        .optional(query.optional())
                        .description(description == null ? "" : description.value())
                        .build()
                );
            }
        }
        return queryParameters;
    }

    <T> void visitBody(BodyVisitor<T> visitor, T input) {
        Class<?> bodyType = null;
        String explanation = "";
        for (int i = 0; i < method.getParameterCount(); i++) {
            Body body = method.getParameters()[i].getAnnotation(Body.class);
            if (body != null) {
                bodyType = method.getParameterTypes()[i];
                Description description = method.getParameters()[i].getAnnotation(Description.class);
                explanation = (description == null) ? "" : description.value();
                break;
            }
        }
        if (bodyType == String.class) {
            visitor.visitString(explanation, input);
        } else if (bodyType == byte[].class) {
            visitor.visitBytes(explanation, input);
        } else if (bodyType != null) {
            visitor.visitJson(bodyType, explanation, input);
        }
    }

    <T> void visitResponse(ResponseVisitor<T> visitor, T input) {
        Responses responses = method.getAnnotation(Responses.class);
        if (responses != null) {
            final Class<?>[] classes = responses.responses();
            final String[] explanations = responses.explanations();
            for (int i = 0; i < classes.length; i++) {
                final Class<?> responseType = classes[i];
                visitor.beforeVisit(i, classes.length, input);
                visitResponseType(responseType, explanations[i], visitor, input);
            }
        } else {
            visitor.beforeVisit(0, 1, input);
            visitResponseType(method.getReturnType(), "", visitor, input);
        }
    }

    private <T> void visitResponseType(Class<?> type, String explanation, ResponseVisitor<T> visitor, T input) {
        if (type == String.class) {
            visitor.visitString(explanation, input);
        } else if (type == byte[].class) {
            visitor.visitBytes(explanation, input);
        } else if (type == ResponseData.class) {
            visitor.visitData(explanation, input);
        } else {
            visitor.visitJson(type, explanation, input);
        }
    }

    interface BodyVisitor<T> {
        void visitJson(Class<?> type, String explanation, T input);

        void visitString(String explanation, T input);

        void visitBytes(String explanation, T input);
    }

    interface ResponseVisitor<T> {
        void beforeVisit(int currentIndex, int totalCount, T input);

        void visitString(String explanation, T input);

        void visitBytes(String explanation, T input);

        void visitData(String explanation, T input);

        void visitJson(Class<?> type, String explanation, T input);
    }
}
