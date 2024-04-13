package vault;

import java.util.function.BiFunction;

public class Edge {
    private final Node endNode;
    private final Position position;
    private final BiFunction<Integer, Integer, Integer> function;

    public Edge(Node endNode, Position position, BiFunction<Integer, Integer, Integer> function) {
        this.endNode = endNode;
        this.position = position;
        this.function = function;
    }

    public int getTransitionValue(int currentValue) {
        return function.apply(currentValue, endNode.getValue());
    }

    public Node getEndNode() {
        return endNode;
    }

    public Position getOperator() {
        return position;
    }
}
