package doc;

class DocumentationQueryParameter {

    final String name;
    final Class<?> type;
    final String description;
    final boolean optional;

    private DocumentationQueryParameter(Builder builder) {
        name = builder.name;
        type = builder.type;
        description = builder.description;
        optional = builder.optional;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String name;
        private Class<?> type;
        private String description;
        private boolean optional;

        Builder name(String name) {
            this.name = name;
            return this;
        }

        Builder type(Class<?> type) {
            this.type = type;
            return this;
        }

        Builder description(String description) {
            this.description = description;
            return this;
        }

        Builder optional(Boolean optional) {
            this.optional = optional;
            return this;
        }

        DocumentationQueryParameter build() {
            return new DocumentationQueryParameter(this);
        }
    }
}
