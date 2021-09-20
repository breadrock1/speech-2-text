package doc;

import org.apache.commons.text.StringEscapeUtils;

@SuppressWarnings("UnusedReturnValue")
class Html {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public String toString() {
        return builder.toString();
    }

    Html css(String value) {
        tag("style", value);
        builder.append("\n");
        return this;
    }

    Html h1(String value) {
        tag("h1", value);
        builder.append("\n");
        return this;
    }

    Html h2(String value, String anchor) {
        tag("h2", "id='" + anchor + "'", value);
        builder.append("\n");
        return this;
    }

    Html h3(String value) {
        tag("h3", value);
        builder.append("\n");
        return this;
    }

    Html b(String value) {
        tag("b", value);
        return this;
    }

    Html i(String value) {
        tag("i", value);
        return this;
    }

    Html br() {
        builder.append("<br/>\n");
        return this;
    }

    Html space() {
        return text(" ");
    }

    Html text(String text) {
        builder.append(text);
        return this;
    }

    Html startUl() {
        builder.append("<ul>\n");
        return this;
    }

    Html endUl() {
        builder.append("</ul>\n");
        return this;
    }

    Html startLi() {
        builder.append("<li>");
        return this;
    }

    Html endLi() {
        builder.append("</li>\n");
        return this;
    }

    Html pre(String value) {
        tag("pre", value);
        builder.append("\n");
        return this;
    }

    Html startTable() {
        builder.append("<table>");
        builder.append("\n");
        return this;
    }

    Html endTable() {
        builder.append("</table>");
        builder.append("\n");
        return this;
    }

    Html startTr() {
        builder.append("<tr>");
        builder.append("\n");
        return this;
    }

    Html endTr() {
        builder.append("</tr>");
        builder.append("\n");
        return this;
    }

    Html td(String value) {
        tag("td", value);
        return this;
    }

    Html th(String value) {
        tag("th", value);
        return this;
    }

    Html a(String value, String link) {
        tag("a", "href='" + link + "'", value);
        return this;
    }

    Html clear() {
        builder.setLength(0);
        return this;
    }

    private void tag(String tag, String content) {
        builder.append("<").append(tag).append(">")
                .append(StringEscapeUtils.escapeHtml4(content))
                .append("</").append(tag).append(">");
    }

    private void tag(String tag, String attributes, String content) {
        builder.append("<").append(tag).append(" ").append(attributes).append(">")
                .append(StringEscapeUtils.escapeHtml4(content))
                .append("</").append(tag).append(">");
    }
}
