package doc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class DocumentationGroup {
    final String title;
    final List<DocumentationItem> items = new ArrayList<>();

    DocumentationGroup(String title) {
        this.title = title;
    }

    void add(DocumentationItem item) {
        items.add(item);
    }

    List<DocumentationItem> getSortedItems() {
        List<DocumentationItem> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparing(DocumentationItem::getPath));
        return sorted;
    }
}
