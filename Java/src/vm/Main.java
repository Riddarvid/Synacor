package vm;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        /*List<String> instructions = compileProgram(6027, 6068);
        for (String instruction : instructions) {
            System.out.println(instruction);
        }*/
        runVM();
    }

    private static void runVM() {
        List<Integer> program = readMemory();
        VM vm = new VM(program);
        vm.initAutoPlay(readAutoPlay());
        new Thread(vm).start();
        Scanner sc = new Scanner(System.in);
        while (true) {
            vm.addInstruction(sc.nextLine());
        }
    }

    private static void printCallStack(List<String> callStack) {
        try {
            FileWriter fw = new FileWriter("call-stack.txt");
            for (String s : callStack) {
                fw.write(s + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Instruction> compileProgram() {
        List<Integer> initialMemory = readMemory();
        Map<Integer, Instruction> rowToInstruction = new HashMap<>();
        Set<Integer> alreadyCompiled = new HashSet<>();
        compileProgram(0, rowToInstruction, initialMemory, alreadyCompiled);
        for (int i = 0; i < initialMemory.size(); i++) {
            if (!alreadyCompiled.contains(i)) {
                List<Integer> data = new ArrayList<>();
                data.add(initialMemory.get(i));
                rowToInstruction.put(i, new Instruction(i, "data", data));
            }
        }
        List<Instruction> compiledProgram = new ArrayList<>();
        Queue<Instruction> pq = new PriorityQueue<>();
        pq.addAll(rowToInstruction.values());
        while (!pq.isEmpty()) {
            compiledProgram.add(pq.poll());
        }
        return compiledProgram;
    }

    private static void compileProgram(int start, Map<Integer, Instruction> compiledProgram, List<Integer> initialMemory, Set<Integer> alreadyCompiled) {
        int i = start;
        while (i < initialMemory.size() && !alreadyCompiled.contains(i)) {
            int opCode = initialMemory.get(i);
            String name;
            int nOperands;
            switch (opCode) {
                case 0 -> {name = "halt"; nOperands = 0;}
                case 1 -> {name = "set"; nOperands = 2;}
                case 2 -> {name = "push"; nOperands = 1;}
                case 3 -> {name = "pop"; nOperands = 1;}
                case 4 -> {name = "eq"; nOperands = 3;}
                case 5 -> {name = "gt"; nOperands = 3;}
                case 6 -> {name = "jmp"; nOperands = 1;}
                case 7 -> {name = "jt"; nOperands = 2;}
                case 8 -> {name = "jf"; nOperands = 2;}
                case 9 -> {name = "add"; nOperands = 3;}
                case 10 -> {name = "mult"; nOperands = 3;}
                case 11 -> {name = "mod"; nOperands = 3;}
                case 12 -> {name = "and"; nOperands = 3;}
                case 13 -> {name = "or"; nOperands = 3;}
                case 14 -> {name = "not"; nOperands = 2;}
                case 15 -> {name = "rmem"; nOperands = 2;}
                case 16 -> {name = "wmem"; nOperands = 2;}
                case 17 -> {name = "call"; nOperands = 1;}
                case 18 -> {name = "ret" ; nOperands = 0;}
                case 19 -> {name = "out"; nOperands = 1;}
                case 20 -> {name = "in"; nOperands = 1;}
                case 21 -> {name = "noop"; nOperands = 0;}
                default -> throw new InputMismatchException("Not an opcode");
            }
            List<Integer> operands = new ArrayList<>();
            alreadyCompiled.add(i);
            for (int j = 0; j < nOperands; j++) {
                alreadyCompiled.add(i + j + 1);
                operands.add(initialMemory.get(i + j + 1));
            }
            Instruction instruction = new Instruction(i, name, operands);
            compiledProgram.put(i, instruction);
            if (opCode == 6 || opCode == 17) {
                if (operands.get(0) == 843) {
                    System.out.println();
                }
                compileProgram(operands.get(0), compiledProgram, initialMemory, alreadyCompiled);
            } else if (opCode == 7 || opCode == 8) {
                if (operands.get(1) == 843) {
                    System.out.println();
                }
                compileProgram(operands.get(1), compiledProgram, initialMemory, alreadyCompiled);
            }
            if (opCode == 0 || opCode == 6 || opCode == 18) {
                break;
            }
            i += nOperands + 1;
        }
    }

    private static List<String> compileProgram(int start, int end) {
        List<Integer> initialMemory = readMemory();
        List<String> compiledProgram = new ArrayList<>();
        int i = start;
        while (i < end) {
            StringBuilder sb = new StringBuilder();
            sb.append(i).append(": ");
            int opCode = initialMemory.get(i);
            String name;
            int nOperands;
            switch (opCode) {
                case 0 -> {name = "halt"; nOperands = 0;}
                case 1 -> {name = "set"; nOperands = 2;}
                case 2 -> {name = "push"; nOperands = 1;}
                case 3 -> {name = "pop"; nOperands = 1;}
                case 4 -> {name = "eq"; nOperands = 3;}
                case 5 -> {name = "gt"; nOperands = 3;}
                case 6 -> {name = Constants.ANSI_RED + "jmp" + Constants.ANSI_RESET; nOperands = 1;}
                case 7 -> {name = Constants.ANSI_RED + "jt" + Constants.ANSI_RESET; nOperands = 2;}
                case 8 -> {name = Constants.ANSI_RED + "jf" + Constants.ANSI_RESET; nOperands = 2;}
                case 9 -> {name = "add"; nOperands = 3;}
                case 10 -> {name = "mult"; nOperands = 3;}
                case 11 -> {name = "mod"; nOperands = 3;}
                case 12 -> {name = "and"; nOperands = 3;}
                case 13 -> {name = "or"; nOperands = 3;}
                case 14 -> {name = "not"; nOperands = 2;}
                case 15 -> {name = "rmem"; nOperands = 2;}
                case 16 -> {name = "wmem"; nOperands = 2;}
                case 17 -> {name = Constants.ANSI_RED + "call" + Constants.ANSI_RESET; nOperands = 1;}
                case 18 -> {name = Constants.ANSI_RED + "ret" + Constants.ANSI_RESET; nOperands = 0;}
                case 19 -> {name = "out"; nOperands = 1;}
                case 20 -> {name = "in"; nOperands = 1;}
                case 21 -> {name = "noop"; nOperands = 0;}
                default -> {name = "data " + opCode; nOperands = 0;}
            }
            sb.append(name).append(' ');
            for (int j = 0; j < nOperands; j++) {
                int operand = initialMemory.get(i + j + 1);
                String operandString;
                if (operand < 32768) {
                    if (name.equals("out")) {
                        operandString = "'" + ((char)(operand)) + "'";
                    } else {
                        operandString = operand + "";
                    }
                } else if (operand < 32776) {
                    operandString = "reg" + (operand - 32768);
                    if (operand == 32775) {
                        operandString = Constants.ANSI_RED + operandString + Constants.ANSI_RESET;
                    }
                } else {
                    operandString = "invalid";
                }
                sb.append(operandString).append(' ');
            }
            compiledProgram.add(sb.toString());
            i += nOperands + 1;
        }
        return compiledProgram;
    }

    private static List<String> readAutoPlay() {
        try {
            return Files.readAllLines(Path.of("autoplay.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Integer> readMemory() {
        try {
            byte[] bytes = Files.readAllBytes(Path.of("challenge.bin"));
            List<Integer> memory = new ArrayList<>();
            for (int i = 0; i < bytes.length; i += 2) {
                int lowByte = (bytes[i] + 256) % 256;
                int highByte = (bytes[i + 1] + 256) % 256;
                int value = lowByte + highByte * 256;
                memory.add(value);
            }
            return memory;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
