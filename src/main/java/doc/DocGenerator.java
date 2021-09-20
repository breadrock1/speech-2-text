package doc;

import server.http.HandlerClassWalker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static server.util.UrlUtils.concatPath;

public class DocGenerator {

    private final Html html = new Html();
    private final List<GroupDescription> groupDescriptions = new ArrayList<>();
    private final Map<String, String> groupIdMap = new HashMap<>();

    private String baseUrl = "";

    public DocGenerator setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public DocGenerator addGroup(String title, String pathRegex) {
        groupDescriptions.add(new GroupDescription(title, Pattern.compile(pathRegex)));
        groupIdMap.put(title, "group" + groupIdMap.size());
        return this;
    }

    public String generate(List<Class<?>> classList) {
        html.clear();
        html.css("table, th, td {\n" +
                "  border: 1px solid black;\n" +
                "  border-collapse: collapse;\n" +
                "  padding: 4px 8px 4px 8px;\n" +
                "}");

        html.h1("Summary API");
        for (GroupDescription description : groupDescriptions) {
            html.a(description.title, "#" + groupIdMap.get(description.title)).br();
        }

        Map<String, DocumentationGroup> groups = splitIntoGroups(buildItems(classList));
        for (GroupDescription description : groupDescriptions) {
            DocumentationGroup group = groups.get(description.title);
            if (group != null) {
                printGroup(group);
                groups.remove(description.title);
            }
        }

        for (DocumentationGroup group : groups.values()) {
            printGroup(group);
        }

        return html.toString();
    }

    private List<DocumentationItem> buildItems(List<Class<?>> classList) {
        List<DocumentationItem> allItems = new ArrayList<>(classList.size());
        for (Class<?> clazz : classList) {
            new HandlerClassWalker((httpMethod, path, method) ->
                    allItems.add(new DocumentationItem(httpMethod, path, method))
            ).walk(clazz);
        }
        return allItems;
    }

    private Map<String, DocumentationGroup> splitIntoGroups(List<DocumentationItem> items) {
        Map<String, DocumentationGroup> groupMap = new HashMap<>();
        for (DocumentationItem item : items) {
            groupMap.computeIfAbsent(findGroupFor(item), DocumentationGroup::new).add(item);
        }
        return groupMap;
    }

    private String findGroupFor(DocumentationItem item) {
        for (GroupDescription description : groupDescriptions) {
            if (description.pathRegex.matcher(item.getPath()).matches()) {
                return description.title;
            }
        }
        return "Прочее";
    }

    private void printGroup(DocumentationGroup group) {
        html.h2(group.title, groupIdMap.get(group.title));
        List<DocumentationItem> sortedItems = group.getSortedItems();
        for (DocumentationItem item : sortedItems) {
            printItem(item);
        }
    }

    private void printItem(DocumentationItem item) {
        html.h3(item.getHttpMethod() + " " + concatPath(baseUrl, item.getPath()))
                .text("Описание: ").text(item.getDescription()).br()
                .text("Требуется авторизация: ").text(item.isAuthorized() ? "Да" : "Нет").br().br();

        if (!item.getQueryParameters().isEmpty()) {
            html.text("Query-параметры:").br();
            html.startTable().th("Имя").th("Тип").th("Обязательный").th("Описание");
            item.getQueryParameters().forEach(param -> html.startTr()
                    .td(param.name)
                    .td(getQueryParameterType(param.type))
                    .td(param.optional ? "Нет" : "Да")
                    .td(param.description)
                    .endTr());
            html.endTable().br();
        }

        item.visitBody(new DocumentationItem.BodyVisitor<Html>() {
            @Override
            public void visitJson(Class<?> type, String explanation, Html input) {
                input.text("Запрос: JSON, ").text(type.getSimpleName());
                if (!explanation.isEmpty()) {
                    html.text(" - ").text(explanation);
                }
                html.br().br();
                new JsonPrinter().print(type, input);
                html.br();
            }

            @Override
            public void visitString(String explanation, Html input) {
                input.text("Запрос: Plain text");
                if (!explanation.isEmpty()) {
                    html.text(" - ").text(explanation);
                }
                html.br().br();
            }

            @Override
            public void visitBytes(String explanation, Html input) {
                input.text("Запрос: Raw binary");
                if (!explanation.isEmpty()) {
                    html.text(" - ").text(explanation);
                }
                html.br().br();
            }
        }, html);

        item.visitResponse(new DocumentationItem.ResponseVisitor<Html>() {

            @Override
            public void beforeVisit(int currentIndex, int totalCount, Html input) {
                if (currentIndex > 0) {
                    html.br();
                }
            }

            @Override
            public void visitString(String explanation, Html input) {
                html.text("Ответ: Plain text");
                if (!explanation.isEmpty()) {
                    html.text(" - ").text(explanation);
                }
                html.br();
            }

            @Override
            public void visitBytes(String explanation, Html input) {
                input.text("Ответ: Raw binary");
                if (!explanation.isEmpty()) {
                    html.text(" - ").text(explanation);
                }
                html.br();
            }

            @Override
            public void visitData(String explanation, Html input) {
                input.text("Ответ: Data").br();
                if (!explanation.isEmpty()) {
                    html.text(" - ").text(explanation);
                }
                html.br();
            }

            @Override
            public void visitJson(Class<?> type, String explanation, Html input) {
                input.text("Ответ: JSON ").text(type.getSimpleName());
                if (!explanation.isEmpty()) {
                    html.text(" - ").text(explanation);
                }
                html.br().br();
                new JsonPrinter().print(type, input);
            }
        }, html);

        html.br();
    }

    private static String getQueryParameterType(Class<?> type) {
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
        throw new IllegalArgumentException("Unsupported query parameter type: " + type);
    }

    private static class GroupDescription {
        final String title;
        final Pattern pathRegex;

        private GroupDescription(String title, Pattern pathRegex) {
            this.title = title;
            this.pathRegex = pathRegex;
        }
    }

}
