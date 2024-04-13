package vm;

import java.util.InputMismatchException;
import java.util.List;

public class Instruction implements Comparable<Instruction> {
    private final int row;
    private final String name;
    private final List<Integer> operands;

    public Instruction(int row, String name, List<Integer> operands) {
        this.row = row;
        this.name = name;
        this.operands = operands;
    }

    @Override
    public int compareTo(Instruction instruction) {
        return row - instruction.row;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(row).append(": ").append(name).append(" ");
        if (name.equals("out")) {
            sb.append(((char)(operands.get(0).intValue()) + ""));
        } else {
            for (int operand : operands) {
                if (operand < 32768) {
                    sb.append(operand).append(" ");
                } else if (operand < 32776) {
                    sb.append("reg").append(operand - 32768).append(" ");
                } else {
                    throw new InputMismatchException("Illegal number");
                }
            }
        }
        return sb.toString();
    }
}
