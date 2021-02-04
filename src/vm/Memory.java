package vm;

import java.util.List;

public class Memory {
    private final int[] contents;

    public Memory(int size) {
        contents = new int[size];
    }

    public Memory(int size, List<Integer> initialMemory) {
        contents = new int[size];
        for (int i = 0; i < initialMemory.size(); i++) {
            contents[i] = initialMemory.get(i);
        }
    }

    public int get(int address) {
        return contents[address];
    }

    public void set(int address, int value) {
        contents[address] = value;
    }

    public int size() {
        return contents.length;
    }
}
