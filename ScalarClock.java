import java.io.Serializable;

public class ScalarClock implements Serializable {
    private int clock;
    private final int processId;

    public ScalarClock(int processId) {
        this.processId = processId;
        this.clock = 0;
    }

    public synchronized void localEvent() {
        clock++;
        System.out.println("Process " + processId + " - Scalar Clock updated to: " + clock);
    }

    public synchronized int sendEvent() {
        clock++;
        System.out.println("Process " + processId + " - Scalar Clock updated to: " + clock + " (send)");
        return clock;
    }

    public synchronized void receiveEvent(int receivedClock) {
        if (receivedClock != -1) {
            clock = Math.max(clock, receivedClock) + 1;
        }
        System.out.println("Process " + processId + " - Scalar Clock updated to: " + clock + " (receive)");
    }
}