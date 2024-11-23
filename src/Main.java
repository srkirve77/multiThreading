public class Main {
    public final static ScheduledCallBackService scheduledCallBackService = new ScheduledCallBackService();
    public static void main(String[] args) throws InterruptedException {
        scheduledCallBackService.initExecutionThread();
        scheduledCallBackService.startClientThreads();
    }
}