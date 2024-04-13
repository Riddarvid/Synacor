package vault;

import java.util.*;
import java.util.function.BiFunction;

public class Breaker {
    private final String[][] grid = {
            {"22", "-", "9", "*"},
            {"+", "4", "-", "18"},
            {"4", "*", "11", "*"},
            {"*", "8", "-", "1"}
    };

    public static void main(String[] args) {
        new Breaker().run();
    }

    private void run() {
        initializeGraph();
    }

    private void initializeGraph() {

        Map<Position, Node> nodeMap = new HashMap<>();
        Map<Position, BiFunction<Integer, Integer, Integer>> operatorMap = new HashMap<>();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if ((x + y) % 2 == 0) {
                    nodeMap.put(new Position(x, y), new Node(new Position(x, y), Integer.parseInt(grid[y][x])));
                } else {
                    operatorMap.put(new Position(x, y), parseOperator(grid[y][x]));
                }
            }
        }

        for (Position position : nodeMap.keySet()) {
            addAllEdges(position, nodeMap, operatorMap);
        }

        State startState = new State(22, nodeMap.get(new Position(0, 0)), "(0, 0)");
        State goalState = new State(30, nodeMap.get(new Position(3, 3)), null);

        Set<State> states = new HashSet<>();
        states.add(startState);

        while (!states.contains(goalState)) {
            Set<State> newStates = new HashSet<>();
            for (State state : states) {
                newStates.addAll(state.getNeighbours());
            }
            states = newStates;
        }

        State endState = null;
        for (State state : states) {
            if (state.equals(goalState)) {
                endState = state;
            }
        }
        if (endState == null) {
            throw new InputMismatchException();
        }

        endState.printPath();
    }

    private BiFunction<Integer, Integer, Integer> parseOperator(String string) {
        switch (string) {
            case "+" -> {return (a, b) -> (a + b);}
            case "-" -> {return (a, b) -> (a - b);}
            case "*" -> {return (a, b) -> (a * b);}
            default -> throw new InputMismatchException();
        }
    }

    private void addAllEdges(Position position, Map<Position, Node> nodeMap, Map<Position, BiFunction<Integer, Integer, Integer>> operatorMap) {
        int x = position.getX();
        int y = position.getY();

        List<Position> operators = new ArrayList<>();
        if (y + 1 <= 3) {
            operators.add(new Position(x, y + 1));
        }
        if (y - 1 >= 0) {
            operators.add(new Position(x, y - 1));
        }
        if (x + 1 <= 3) {
            operators.add(new Position(x + 1, y));
        }
        if (x - 1 >= 0) {
            operators.add(new Position(x - 1, y));
        }

        for (Position operator : operators) {
            addEdgesOperator(position, operator, nodeMap, operatorMap);
        }
    }

    private void addEdgesOperator(Position position, Position operator, Map<Position, Node> nodeMap, Map<Position, BiFunction<Integer, Integer, Integer>> operatorMap) {
        int x = operator.getX();
        int y = operator.getY();


        Position startPosition = new Position(0, 0);

        List<Position> targets = new ArrayList<>();
        Position potentialTarget = new Position(x, y + 1);
        if (y + 1 <= 3 && !potentialTarget.equals(position) && !potentialTarget.equals(startPosition)) {
            targets.add(potentialTarget);
        }
        potentialTarget = new Position(x, y - 1);
        if (y - 1 >= 0 && !potentialTarget.equals(position) && !potentialTarget.equals(startPosition)) {
            targets.add(potentialTarget);
        }
        potentialTarget = new Position(x + 1, y);
        if (x + 1 <= 3 && !potentialTarget.equals(position) && !potentialTarget.equals(startPosition)) {
            targets.add(potentialTarget);
        }
        potentialTarget = new Position(x - 1, y);
        if (x - 1 >= 0 && !potentialTarget.equals(position) && !potentialTarget.equals(startPosition)) {
            targets.add(potentialTarget);
        }

        Node sourceNode = nodeMap.get(position);
        BiFunction<Integer, Integer, Integer> function = operatorMap.get(operator);
        for (Position target : targets) {
            sourceNode.addEdge(new Edge(nodeMap.get(target), operator, function));
        }
    }
}
