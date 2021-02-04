package vm;

import java.util.*;

public class VM implements Runnable {
    private final static int BITMASK = (int) (Math.pow(2, 15) - 1);

    private final Scanner scanner = new Scanner(System.in);
    private final LinkedList<Character> inputQueue = new LinkedList<>();
    private final LinkedList<String> autoPlayQueue = new LinkedList<>();
    private final Map<Integer, Set<Integer>> subRoutines = new HashMap<>();
    private final List<String> subRoutineStack = new ArrayList<>();

    private final boolean debug = true;
    private boolean shouldLogSubRoutines = false;

    private final Memory memory;
    private final Registry registry;
    private final Stack<Integer> stack;
    private int pc;
    private boolean halted;

    public VM(List<Integer> initialMemory) {
        int memorySize = (int) Math.pow(2, 15);
        this.memory = new Memory(memorySize, initialMemory);
        int registrySize = 8;
        this.registry = new Registry(registrySize);
        this.stack = new Stack<>();
        this.pc = 0;
        this.halted = false;
    }

    public void run() {
        while (!halted && pc < memory.size()) {
            executeNext();
        }
        if (halted) {
            System.out.println("Program terminated by halt instruction");
        } else {
            System.out.println("Program terminated by out of instructions");
        }
    }

    private void executeNext() {
        if (pc == 5489) {
            pc = 5498;
        }
        int instruction = memory.get(pc);
        switch (instruction) {
            case 0 -> halt();
            case 1 -> set();
            case 2 -> push();
            case 3 -> pop();
            case 4 -> eq();
            case 5 -> gt();
            case 6 -> jmp();
            case 7 -> jt();
            case 8 -> jf();
            case 9 -> add();
            case 10 -> mult();
            case 11 -> mod();
            case 12 -> and();
            case 13 -> or();
            case 14 -> not();
            case 15 -> rmem();
            case 16 -> wmem();
            case 17 -> call();
            case 18 -> ret();
            case 19 -> out();
            case 20 -> in();
            case 21 -> noop();
            default -> throw new IllegalArgumentException("Invalid instruction " + instruction);
        }
    }

    private void halt() {
        halted = true;
    }

    private void set() {
        pc++;
        int register = getRegister();
        pc++;
        int value = getValue();
        registry.set(register, value);
        pc++;
    }

    private void push() {
        pc++;
        int value = getValue();
        stack.push(value);
        pc++;
    }

    private void pop() {
        if (stack.empty()) {
            throw new IllegalStateException("Empty stack");
        }
        pc++;
        int register = getRegister();
        registry.set(register, stack.pop());
        pc++;
    }

    private void eq() {
        pc++;
        int register = getRegister();
        pc++;
        int b = getValue();
        pc++;
        int c = getValue();
        if (b == c) {
            registry.set(register, 1);
        } else {
            registry.set(register, 0);
        }
        pc++;
    }

    private void gt() {
        pc++;
        int register = getRegister();
        pc++;
        int b = getValue();
        pc++;
        int c = getValue();
        if (b > c) {
            registry.set(register, 1);
        } else {
            registry.set(register, 0);
        }
        pc++;
    }

    private void jmp() {
        pc++;
        pc = getValue();
    }

    private void jt() {
        pc++;
        int condition = getValue();
        pc++;
        int address = getValue();
        if (condition != 0) {
            pc = address;
        } else {
            pc++;
        }
    }

    private void jf() {
        pc++;
        int condition = getValue();
        pc++;
        int address = getValue();
        if (condition == 0) {
            pc = address;
        } else {
            pc++;
        }
    }

    private void add() {
        pc++;
        int register = getRegister();
        pc++;
        int b = getValue();
        pc++;
        int c = getValue();
        registry.set(register, (b + c) % 32768);
        pc++;
    }

    private void mult() {
        pc++;
        int register = getRegister();
        pc++;
        int b = getValue();
        pc++;
        int c = getValue();
        registry.set(register, (b * c) % 32768);
        pc++;
    }

    private void mod() {
        pc++;
        int register = getRegister();
        pc++;
        int b = getValue();
        pc++;
        int c = getValue();
        registry.set(register, b % c);
        pc++;
    }

    private void and() {
        pc++;
        int register = getRegister();
        pc++;
        int b = getValue();
        pc++;
        int c = getValue();
        registry.set(register, b & c);
        pc++;
    }

    private void or() {
        pc++;
        int register = getRegister();
        pc++;
        int b = getValue();
        pc++;
        int c = getValue();
        registry.set(register, b | c);
        pc++;
    }

    private void not() {
        pc++;
        int register = getRegister();
        pc++;
        int b = getValue();
        registry.set(register, (~b) & BITMASK);
        pc++;
    }

    private void rmem() {
        pc++;
        int register = getRegister();
        pc++;
        int source = getValue();
        registry.set(register, memory.get(source));
        pc++;
    }

    private void wmem() {
        pc++;
        int target = getValue();
        pc++;
        int value = getValue();
        memory.set(target, value);
        pc++;
    }

    private void call() {
        pc++;
        int jumpAddress = getValue();
        //System.out.println(vm.Constants.ANSI_RED + "Jumping to subroutine " + jumpAddress + " from " + (pc - 1) + vm.Constants.ANSI_RESET);
        if (shouldLogSubRoutines) {
            if (subRoutines.containsKey(jumpAddress)) {
                subRoutines.get(jumpAddress).add(pc - 1);
            } else {
                Set<Integer> fromAddresses = new HashSet<>();
                fromAddresses.add(pc - 1);
                subRoutines.put(jumpAddress, fromAddresses);
            }
            subRoutineStack.add("Call " + jumpAddress + " from " + (pc - 1));
        }
        pc++;
        stack.push(pc);
        pc = jumpAddress;
    }

    private void ret() {
        if (stack.isEmpty()) {
            halted = true;
        } else {
            //System.out.println(vm.Constants.ANSI_RED + "Returning from subroutine to " + stack.peek() + vm.Constants.ANSI_RESET);
            pc = stack.pop();
            if (shouldLogSubRoutines) {
                subRoutineStack.add("Ret");
            }
        }
    }

    private void out() {
        pc++;
        int operand = getValue(memory.get(pc));
        System.out.print((char) operand);
        pc++;
    }

    private void in() {
        pc++;
        int register = getRegister();
        synchronized (inputQueue) {
            while (inputQueue.isEmpty()) {
                if (autoPlayQueue.isEmpty()) {
                    try {
                        inputQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    String nextWord = autoPlayQueue.removeFirst();
                    System.out.print(Constants.ANSI_RED + nextWord + Constants.ANSI_RESET);
                    if (nextWord.startsWith("#")) {
                        System.out.println();
                        executeDirective(nextWord);
                    } else {
                        for (char c : nextWord.toCharArray()) {
                            inputQueue.addLast(c);
                        }
                        inputQueue.addLast('\n');
                    }
                }
            }
            char c = inputQueue.removeFirst();
            if (autoPlayQueue.isEmpty() && inputQueue.isEmpty()) {
                shouldLogSubRoutines = true;
            }
            registry.set(register, c);
        }
        pc++;
    }

    private void executeDirective(String directive) {
        directive = directive.substring(1);
        String[] tokens = directive.split(" ");
        if (tokens[0].equals("set")) {
            setRegister(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
        }
    }

    private void noop() {
        pc++;
    }

    private int getValue() {
        return getValue(memory.get(pc));
    }

    private int getValue(int operand) {
        if (operand < 0) {
            throw new IllegalArgumentException("Invalid number " + operand);
        } else if (operand < 32768) {
            return operand;
        } else if (operand < 32776) {
            return registry.get(getRegister(operand));
        } else {
            throw new IllegalArgumentException("Invalid number " + operand);
        }
    }

    private int getRegister() {
        return getRegister(memory.get(pc));
    }

    private int getRegister(int operand) {
        if (operand >= 32768 && operand < 32776) {
            return operand - 32768;
        }
        throw new IllegalArgumentException("Invalid registry " + operand);
    }

    public void initAutoPlay(List<String> autoPlayScript) {
        for (String instruction : autoPlayScript) {
            autoPlayQueue.addLast(instruction);
        }
    }

    public void addInstruction(String instruction) {
        synchronized (inputQueue) {
            for (char c : instruction.toCharArray()) {
                inputQueue.addLast(c);
            }
            inputQueue.addLast('\n');
            inputQueue.notify();
        }
    }

    public void setRegister(int index, int value) {
        registry.set(index, value);
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int i) {
        pc = i;
    }

    public List<String> getCallStack() {
        return subRoutineStack;
    }
}
