public class Main {
    public final static ScheduledCallBackService scheduledCallBackService = new ScheduledCallBackService();
    public final static CounterSemaphoreTest counterSemaphoreTest = new CounterSemaphoreTest();
    public static void main(String[] args) throws InterruptedException {
//        scheduledCallBackService.initExecutionThread();
//        scheduledCallBackService.startClientThreads();
        counterSemaphoreTest.startClientThreads();

    }
}