import java.io.Serializable;

public class Message implements Serializable {
    final int scalarClock;
    final int[] vectorClock;
    final int[][] matrixClock;
    final int senderId;

    public Message(int scalarClock, int[] vectorClock, int[][] matrixClock, int senderId) {
        this.scalarClock = scalarClock;
        this.vectorClock = vectorClock;
        this.matrixClock = matrixClock;
        this.senderId = senderId;
    }
}