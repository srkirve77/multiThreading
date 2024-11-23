import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.Semaphore;

//ThreadSafe scheduled callback mechanism
class CallBack {
    Long executionTime;
    Integer pid;

    public CallBack(Integer pid, long executionTime) {
        this.pid = pid;
        this.executionTime = executionTime;
    }
}

class CallBackComparator implements Comparator<CallBack> {

    // defining compare method
    public int compare(CallBack obj1, CallBack obj2)
    {
        if (Objects.isNull(obj1) || Objects.isNull(obj2)) {
            return 1;
        }
        if (Objects.equals(obj1.executionTime, obj2.executionTime)) return 0;
        return obj1.executionTime >= obj2.executionTime ? 1:-1;
    }
}

class MyRunnable implements Runnable {
    CallBack callBack;
    public static int callBackReceived = 0;
    public static int callBacksDelayed = 0;
    public MyRunnable(CallBack callBack) {
        this.callBack = callBack;
    }

    public long getCurrentIstTime() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.ofHoursMinutes(5, 30));
    }

    public void run() {
        if (getCurrentIstTime() - callBack.executionTime > 3) {
            callBacksDelayed += 1;
        }
        callBackReceived += 1;
        //System.out.println("CallBack Received for PID " + callBack.pid);
    }
}

public class ScheduledCallBackService {

    Queue<CallBack> executionQueue = new PriorityQueue<>(new CallBackComparator());
    Thread executionThread;
    public final static Semaphore executionThreadLock = new Semaphore(1);
    int noOfclients = 100;
    List<Thread> clientThreads = new ArrayList<>();

    public void initExecutionThread() {
        executionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!executionThread.isInterrupted()) {

                }
                while(!executionQueue.isEmpty()) {
                    while (!executionQueue.isEmpty() && executionQueue.peek().executionTime < getCurrentIstTime()) {
                        assert executionQueue.peek() != null;
                        long sleepTime = executionQueue.peek().executionTime - getCurrentIstTime();
                        if (sleepTime > 0) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        CallBack callBack = executionQueue.peek();
                        executionQueue.remove();
                        processCallBack(callBack);
                        clientThreads.get(callBack.pid).interrupt();
                        if (executionQueue.isEmpty()) {
                            break;
                        }
                    }
                }
                //this.run();
               // executionThreadLock.release();
            }
        });
    }

    public void executeExecutionThread() throws InterruptedException {
        executionThread.interrupt();
    }


    public void registerCallBack(CallBack callBack) throws InterruptedException {
        executionThreadLock.acquire();
        executionQueue.add(callBack);
        executeExecutionThread();
        executionThreadLock.release();
    }

    public LocalDateTime getLocalDateIstTime(long epochInstant) {
        return LocalDateTime.ofEpochSecond(epochInstant, 0, ZoneOffset.ofHoursMinutes(5,30));
    }

    public long getCurrentIstTime() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.ofHoursMinutes(5, 30));
    }

    public long getCallBackExecutionTime() {
        long currentTime = getCurrentIstTime();
        return currentTime + new Random().nextLong(0, 1);
    }

    public void processCallBack(CallBack callBack) {
        Runnable runnable = new MyRunnable(callBack);
        new Thread(runnable).start();
    }

    public void startClientThreads() throws InterruptedException {
        executionThread.start();
        for (int i = 0 ; i < noOfclients ; i++) {
            int finalI = i;
            clientThreads.add(new Thread(() -> {
                CallBack callBack = new CallBack(finalI, getCallBackExecutionTime());
                try {
                    registerCallBack(callBack);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }));
        }

        for (int i = 0 ; i < noOfclients ; i++) {
            clientThreads.get(i).start();
        }
        Thread.sleep(15000L);
        System.out.println("total callback sent " + MyRunnable.callBackReceived);
        System.out.println("total delayed callback sent " + MyRunnable.callBacksDelayed);
        //executeExecutionThread();
    }
}
