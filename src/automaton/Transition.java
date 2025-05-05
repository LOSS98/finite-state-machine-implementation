package automaton;

public class Transition {
    private State origin;
    private State destination;
    private char symbol;

    public Transition(State origin, State destination, char symbol) {
        this.origin = origin;
        this.destination = destination;
        this.symbol = symbol;
    }

    public State getOrigin() {
        return origin;
    }

    public State getDestination() {
        return destination;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return origin.getName() + " --" + symbol + "--> " + destination.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transition that = (Transition) obj;
        return symbol == that.symbol && origin.equals(that.origin) && destination.equals(that.destination);
    }

    @Override
    public int hashCode() {
        int result = origin.hashCode();
        result = 31 * result + destination.hashCode();
        result = 31 * result + symbol;
        return result;
    }
}