package vm;

public class Registry {
    private final int[] registers;

    public Registry(int size) {
        registers = new int[size];
    }

    public int get(int index) {
        return registers[index];
    }

    public void set(int index, int value) {
        registers[index] = value;
    }
}
