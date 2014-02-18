package be.fnord.collections.functions;

import java.util.LinkedList;

public class PartitionListElement<T> extends LinkedList<T> {
    public PartitionListElement(PartitionListItem<T> results3) {
        for (T s : results3.get(0)) {
            add(s);
        }
    }

    public PartitionListElement() {
        super();
    }

    private static final long serialVersionUID = 1L;


}
