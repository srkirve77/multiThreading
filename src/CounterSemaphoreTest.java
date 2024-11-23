import java.util.ArrayList;
import java.util.List;

class CountingSemaphore {
    private int permit;
    private final int maxPermit;

    public CountingSemaphore(int permit, int maxPermit) {
        if (maxPermit < permit) {
            throw new IllegalArgumentException();
        }
        this.maxPermit = maxPermit;
        this.permit = permit;
    }

    public synchronized void acquire() throws InterruptedException {
        while (permit == 0) {
            this.wait();
        }
        permit -= 1;
    }

    public synchronized void release() {
        if (permit >= maxPermit) {
            throw new UnsupportedOperationException("trying to take more than allowed permits");
        }
        permit += 1;
        notifyAll();
    }
}

public class CounterSemaphoreTest {
    CountingSemaphore countingSemaphore = new CountingSemaphore(2, 3);
    int noOfClientThreads = 10;

    public void startClientThreads() {
        List<Thread> clientThreads = new ArrayList<>();
        for (int i = 0 ; i < noOfClientThreads ; i++) {
            int finalI = i;
            clientThreads.add(new Thread(() -> {
                 try {
                     countingSemaphore.acquire();
                 } catch (InterruptedException e) {
                     throw new RuntimeException(e);
                 }
                 try {
                     System.out.println("lock acquired by thread " + finalI);
                     Thread.sleep(2000L);
                 } catch (InterruptedException e) {
                     throw new RuntimeException(e);
                 }
                 countingSemaphore.release();
             }));
        }

        for (int i = 0 ; i < noOfClientThreads ; i++) {
            clientThreads.get(i).start();
        }
    }
}
