public class FakeTest {
    public static void main(String[] args) {
        int numRepeats = 1;
        int numThr = 2;
        if (args.length >= 2) {
            numThr = Integer.parseInt(args[0]);
            numRepeats = Integer.parseInt(args[1]);
        }

        System.out.println("numRepeats: " + numRepeats);

        Thread threads[] = new Thread[numThr];
        DualTester tester = new DualTester(numRepeats);


        for (int i = 0; i < numThr; i++) {
            threads[i] = new Thread(tester);
        }

        for (int i = 0; i < numThr; i++) {
            threads[i].start();
        }

        try {
            for (int i = 0; i < numThr; i++) { threads[i].join(); }
        } catch (InterruptedException ex) {
            System.out.println("Something went wrong during joining.");
        }
    }
}
