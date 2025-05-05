package automaton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Automaton {
    private final Set<State> states;
    private final Set<Character> alphabet;
    private State initialState;

    public Automaton() {
        this.states = new HashSet<>();
        this.alphabet = new HashSet<>();
    }

    public Automaton(String fileName) {
        this();
        try {
            loadFromFile(fileName);
        } catch (IOException e) {
            System.err.println("Error loading automaton from file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java automaton.Automaton <automaton_file> [word1 word2 ...]");
            return;
        }

        Automaton automaton = new Automaton(args[0]);
        System.out.println(automaton);

        System.out.println("Is deterministic: " + automaton.isDeterministic());

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                String word = args[i];
                System.out.println("\nTesting word: " + word);
                boolean accepted = automaton.accepts(word);
                System.out.println("Result: " + (accepted ? "Accepted" : "Rejected"));
            }
        }
    }

    public void addState(State state) {
        states.add(state);
        if (state.isInitial()) {
            initialState = state;
        }
    }

    public void addTransition(State origin, State destination, char symbol) {
        if (!states.contains(origin) || !states.contains(destination)) {
            throw new IllegalArgumentException("Origin or destination state does not exist in the automaton");
        }

        alphabet.add(symbol);
        Transition transition = new Transition(origin, destination, symbol);
        origin.addTransition(transition);
        System.out.println("Added symbol '" + symbol + "' to alphabet");
    }

    public Set<State> getStates() {
        return states;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public State getInitialState() {
        return initialState;
    }

    public Set<State> getFinalStates() {
        return states.stream().filter(State::isFinal).collect(Collectors.toSet());
    }

    public boolean isDeterministic() {
        return states.stream().allMatch(state -> alphabet.stream().allMatch(symbol -> state.getTransitionsForSymbol(symbol).size() <= 1));
    }

    public boolean accepts(String word) {
        if (initialState == null) {
            System.out.println("Rejection: The automaton has no initial state");
            return false;
        }

        State currentState = initialState;
        for (int i = 0; i < word.length(); i++) {
            char symbol = word.charAt(i);


            if (symbol == 'e') {
                System.out.println("Processing epsilon transition from state '" + currentState.getName() + "'");
            } else if (!alphabet.contains(symbol)) {
                System.out.println("Rejection: Symbol '" + symbol + "' is not in the automaton's alphabet");
                return false;
            }

            Set<Transition> transitions = currentState.getTransitionsForSymbol(symbol);
            if (transitions.isEmpty()) {
                System.out.println("Rejection: No transition from state '" + currentState.getName() + "' with symbol '" + symbol + "'");
                return false;
            }

            if (transitions.size() > 1) {
                System.out.println("Warning: Non-deterministic transition. Taking first available transition.");
            }

            currentState = transitions.iterator().next().getDestination();
        }

        if (!currentState.isFinal()) {
            System.out.println("Rejection: End of word reached but state '" + currentState.getName() + "' is not a final state");
            return false;
        }

        return true;
    }

    private void loadFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;


            boolean processingStates = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();


                if (line.isEmpty() || line.startsWith("#")) {

                    if (line.contains("# Transitions") || line.equals("Transitions")) {
                        processingStates = false;
                        System.out.println("Switching to transitions section");
                    }
                    continue;
                }

                if (processingStates) {

                    String[] parts = line.split("\\s+");
                    if (parts.length >= 1) {
                        String stateName = parts[0];
                        boolean isInitial = false;
                        boolean isFinal = false;

                        for (int i = 1; i < parts.length; i++) {
                            if ("initial".equalsIgnoreCase(parts[i])) {
                                isInitial = true;
                            } else if ("final".equalsIgnoreCase(parts[i])) {
                                isFinal = true;
                            }
                        }

                        State state = new State(stateName, isInitial, isFinal);
                        addState(state);
                        System.out.println("Added state: " + stateName + " (Initial: " + isInitial + ", Final: " + isFinal + ")");
                    }
                } else {

                    String[] parts = line.split("\\s+");
                    if (parts.length >= 3) {
                        String originName = parts[0];
                        String symbolStr = parts[1];
                        String destinationName = parts[2];

                        if (symbolStr.length() != 1) {
                            System.err.println("Warning: Symbol should be a single character: " + symbolStr);
                            continue;
                        }

                        char symbol = symbolStr.charAt(0);

                        Optional<State> origin = findStateByName(originName);
                        Optional<State> destination = findStateByName(destinationName);

                        if (origin.isPresent() && destination.isPresent()) {
                            addTransition(origin.get(), destination.get(), symbol);
                            System.out.println("Added transition: " + originName + " --" + symbol + "--> " + destinationName);
                        } else {
                            System.err.println("Warning: Could not find states for transition: " + originName + " " + symbolStr + " " + destinationName);
                            if (!origin.isPresent()) {
                                System.err.println("  Origin state '" + originName + "' not found");
                            }
                            if (!destination.isPresent()) {
                                System.err.println("  Destination state '" + destinationName + "' not found");
                            }
                            System.out.println("Available states: " + states.stream().map(State::getName).collect(Collectors.joining(", ")));
                        }
                    }
                }
            }
        }
    }

    private Optional<State> findStateByName(String name) {
        return states.stream().filter(state -> state.getName().equals(name)).findFirst();
    }

    public Automaton toDeterministic() {
        if (isDeterministic()) {
            return this;
        }

        Automaton deterministicAutomaton = new Automaton();


        Map<Set<State>, State> subsetMap = new HashMap<>();


        Queue<Set<State>> queue = new LinkedList<>();


        Set<State> initialSubset = new HashSet<>();
        initialSubset.add(initialState);


        State newInitialState = new State("q0", true, initialState.isFinal());
        deterministicAutomaton.addState(newInitialState);


        subsetMap.put(initialSubset, newInitialState);
        queue.add(initialSubset);

        int stateCounter = 1;


        while (!queue.isEmpty()) {
            Set<State> currentSubset = queue.poll();
            State currentState = subsetMap.get(currentSubset);


            for (char symbol : alphabet) {
                Set<State> nextSubset = new HashSet<>();


                for (State state : currentSubset) {

                    Set<Transition> transitions = state.getTransitionsForSymbol(symbol);


                    for (Transition transition : transitions) {
                        nextSubset.add(transition.getDestination());
                    }
                }


                if (nextSubset.isEmpty()) {
                    continue;
                }


                if (!subsetMap.containsKey(nextSubset)) {

                    boolean isFinal = nextSubset.stream().anyMatch(State::isFinal);


                    State newState = new State("q" + stateCounter++, false, isFinal);
                    deterministicAutomaton.addState(newState);


                    subsetMap.put(nextSubset, newState);
                    queue.add(nextSubset);
                }


                deterministicAutomaton.addTransition(currentState, subsetMap.get(nextSubset), symbol);
            }
        }

        return deterministicAutomaton;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Automaton:\n");

        sb.append("States:\n");
        for (State state : states) {
            sb.append(" - ").append(state).append("\n");
        }

        sb.append("Alphabet: ");
        sb.append(alphabet.stream().sorted().map(String::valueOf).collect(Collectors.joining(", ")));
        sb.append("\n");

        sb.append("Transitions:\n");
        for (State state : states) {
            for (Transition transition : state.getOutgoingTransitions()) {
                sb.append(" - ").append(transition).append("\n");
            }
        }

        return sb.toString();
    }
}