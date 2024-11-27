import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class ReaderWriter {
    private int data = 0;
    List<Semaphore> readerLocks = new ArrayList<>();
    private int noOfReaders;
    public ReaderWriter(int noOfReaders) {
        this.noOfReaders = noOfReaders;
        for (int i = 0; i < noOfReaders; i++) {
            readerLocks.add(new Semaphore(1));
        }
    }

//    public synchronized void acquireWriteLock() {
//
//    }

    public void write(int writer, int noOfReaders) throws InterruptedException {
        for (int i = 0; i < noOfReaders; i++) {
            readerLocks.get(i).acquire();
        }
        data += 1;
        System.out.println("writer no " + writer + " writing value : " + data);
        Thread.sleep(6000L);
        for (int i = 0; i < noOfReaders; i++) {
            readerLocks.get(i).release();
        }
        //notifyAll();
    }

    public void read(int reader) throws InterruptedException {
        readerLocks.get(reader).acquire();
        System.out.println("Reader no " + reader +" Read value : " + data);
        Thread.sleep(2000L);
        readerLocks.get(reader).release();
        Thread.sleep(1000L);
    }
}


public class ReadWriteTest {
    private int noOfReaders = 5;
    private int noOfWriters = 5;
    List<Thread> readers = new ArrayList<>();
    List<Thread> writers = new ArrayList<>();
    ReaderWriter readerWriter = new ReaderWriter(noOfReaders);
    List<Semaphore> readerLocks = new ArrayList<>();
    List<Semaphore> writerLocks = new ArrayList<>();

    public void testReaderWriter() throws InterruptedException {
        for (int i = 0; i < noOfReaders; i++) {
            int finalI = i;
            readers.add(new Thread(() -> {
                while(true) {
                    try {
                        readerWriter.read(finalI);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
        }

        for (int i = 0; i < noOfWriters; i++) {
            int finalI = i;
            writers.add(new Thread(() -> {
                while (true) {
                    try {
                        readerWriter.write(finalI, noOfReaders);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
        }


            for (int i = 0; i < noOfReaders; i++) {
                readers.get(i).start();
            }

            for (int i = 0; i < noOfWriters; i++) {
                writers.get(i).start();
            }

            for (int i = 0; i < noOfReaders; i++) {
                readers.get(i).join();
            }

            for (int i = 0; i < noOfWriters; i++) {
                writers.get(i).join();
            }
    }

}
