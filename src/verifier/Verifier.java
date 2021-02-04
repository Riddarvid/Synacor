package verifier;


import java.util.Stack;

public class Verifier {
    private int r7;

    private Integer[][] results;

    private int verify(int m, int n) {
        m = m % 32768;
        n = n % 32768;
        if (results[m][n] != null) {
            return results[m][n];
        }
        int result;
        if (m == 0) {
            result = n + 1;
        } else if (n == 0) {
            result = verify(m + 32767, r7);
        } else {
            result = verify(m + 32767, verify(m, n + 32767));
        }
        results[m][n] = result;
        return result;
    }

    private int verifyStack(int m, int n) {
        Stack<Integer> stack = new Stack<>();
        stack.push(m);
        while (!stack.isEmpty()) {
            if (stack.peek() == 0) {
                n++;
                stack.pop();
            } else if (n == 0) {
                m = stack.pop();
                stack.push(m - 1);
                n = r7;
            } else {
                m = stack.pop();
                stack.push(m - 1);
                stack.push(m);
                n--;
            }
        }
        return n;
    }

    private void run() {
        r7 = 1;
        while (r7 < 32768) {
            resetResults();
            int result = ackermann41();
            if (result == 6) {
                System.out.println(r7);
                break;
            } else {
                //System.out.println("r7 = " + r7 + " -> " + result);
            }
            r7++;
        }
    }

    private void resetResults() {
        results = new Integer[5][32768];
        for (int i = 0; i <= 4; i++) {
            results[i] = new Integer[32768];
        }
    }

    private int ackermann41() {
        for (int m = 0; m < 4; m++) {
            for (int n = 0; n < 32768; n++) {
                verify(m, n);
            }
        }
        verify(4, 0);
        return verify(4, 1);
    }

    public static void main(String[] args) {
        new Verifier().run();
    }
}
