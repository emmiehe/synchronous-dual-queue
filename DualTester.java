import java.util.Random;

import java.util.*;

public class DualTester implements Runnable {

    private final SynchronousDualQueue<Object> queue;
    private final int numRepeats;

    DualTester (int repeats) {
        queue = new SynchronousDualQueue<Object>();
        numRepeats = repeats;
    }

    public void run() {
        // Run your tests here!
        for (int i=0; i<numRepeats; i++) {
            long tid = Thread.currentThread().getId();
            if (tid%2 == 0){
                queue.enq(null);
            } else {
                queue.deq();
            }
        }
    }

}