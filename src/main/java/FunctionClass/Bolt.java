package FunctionClass;

public class Bolt {

    public enum BoltState {
        OPEN,
        CLOSED,
        MALFUNCTIONED
    }

    private BoltState state;

    public Bolt() {
        this.state = BoltState.CLOSED;
    }

    public void open() {
        if (!malfunctioned()) {
            this.state = BoltState.OPEN;
        }
    }

    public void close() {
        if (!malfunctioned()) {
            this.state = BoltState.CLOSED;
        }
    }

    public void malfunction() {
        System.out.println("Bolt set to malfunctioned!");
        this.state = BoltState.MALFUNCTIONED;
    }

    public void clearMalfunction() {
        System.out.println("Bolt cleared of malfunction!");
        this.state = BoltState.OPEN;
    }

    public BoltState getState() {
        return this.state;
    }

    public boolean malfunctioned() {
        if (this.state == BoltState.MALFUNCTIONED) {
            System.out.println("Bolt is malfunctioned!");
            return true;
        } else {
            return false;
        }
    }

}
