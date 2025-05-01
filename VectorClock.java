import java.io.Serializable;
import java.util.Arrays;

public class VectorClock implements Serializable {
    private final int[] clock;
    private final int processId;
    private final int numProcesses;

    public VectorClock(int processId, int numProcesses) {
        this.processId = processId;
        this.numProcesses = numProcesses;
        this.clock = new int[numProcesses];
    }

    public synchronized void localEvent() {
        clock[processId]++;
        System.out.println("Process " + processId + " - Vector Clock updated to: " + Arrays.toString(clock));
    }

    public synchronized int[] sendEvent() {
        clock[processId]++;
        System.out.println("Process " + processId + " - Vector Clock updated to: " + Arrays.toString(clock) + " (send)");
        return clock.clone();
    }

    public synchronized void receiveEvent(int[] receivedClock) {
        for (int i = 0; i < numProcesses; i++) {
            clock[i] = Math.max(clock[i], receivedClock[i]);
        }
        clock[processId]++;
        System.out.println("Process " + processId + " - Vector Clock updated to: " + Arrays.toString(clock) + " (receive)");
    }
}