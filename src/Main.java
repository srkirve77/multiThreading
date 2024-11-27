public class Main {
    public final static ScheduledCallBackService scheduledCallBackService = new ScheduledCallBackService();
    public final static CounterSemaphoreTest counterSemaphoreTest = new CounterSemaphoreTest();
    public final static ReadWriteTest readWriteTest = new ReadWriteTest();
    public static void main(String[] args) throws InterruptedException {
//        scheduledCallBackService.initExecutionThread();
//        scheduledCallBackService.startClientThreads();
        readWriteTest.testReaderWriter();

    }
}