package automaton;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class State {
    private final String name;
    private final boolean isInitial;
    private boolean isFinal;
    private final Set<Transition> outgoingTransitions;

    public State(String name, boolean isInitial, boolean isFinal) {
        this.name = name;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
        this.outgoingTransitions = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public Set<Transition> getOutgoingTransitions() {
        return outgoingTransitions;
    }

    public void addTransition(Transition transition) {
        outgoingTransitions.add(transition);
    }

    public Set<Transition> getTransitionsForSymbol(char symbol) {
        return outgoingTransitions.stream().filter(transition -> transition.getSymbol() == symbol).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return name + (isInitial ? "(Initial)" : "") + (isFinal ? "(Final)" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State state = (State) obj;
        return name.equals(state.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}