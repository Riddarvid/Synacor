package vault;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class State {
    private final int value;
    private final Node node;
    private State previousState;
    private final String string;

    public State(int value, Node node, State previousState, String string) {
        this.value = value;
        this.node = node;
        this.previousState = previousState;
        this.string = string;
    }

    public State(int value, Node node, String string) {
        this.value = value;
        this.node = node;
        this.string = string;
    }

    public Set<State> getNeighbours() {
        Set<State> neighbours = new HashSet<>();
        for (Edge edge : node.getEdges()) {
            int newValue = edge.getTransitionValue(value);
            if (newValue >= 0) {
                String pathString = " -> " + edge.getOperator().toString() + " -> " + edge.getEndNode().toString();
                neighbours.add(new State(edge.getTransitionValue(value), edge.getEndNode(), this, pathString));
            }
        }
        return neighbours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return value == state.value && Objects.equals(node, state.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, node);
    }

    public void printPath() {
        if (previousState != null) {
            previousState.printPath();
        }
        System.out.print(string);
    }
}
