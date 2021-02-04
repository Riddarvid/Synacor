package vault;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Node {
    private final Position position;
    private final int value;
    private final Set<Edge> edges = new HashSet<>();

    public Node(Position position, int value) {
        this.position = position;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return value == node.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return position.toString();
    }

    public Set<Edge> getEdges() {
        return edges;
    }
}
