# Finite State Machine Implementation
**TP 2/3 - Khalil MZOUGHI - 3A FISA INFO**

This project implements a Java solution for finite state machines.

## Project Structure

The project consists of several Java classes:

- `State.java`: Represents a state in the automaton with properties for name, initial/final status, and outgoing transitions.
- `Transition.java`: Represents a transition between states with an origin state, destination state, and a symbol.
- `Automaton.java`: The main class that represents the complete automaton, including methods for checking if the automaton is deterministic and if it accepts a given word.
- `TestAutomaton.java`: A test class that loads an automaton and tests it against a set of test cases.

## File Formats

### Automaton File Format

```
# States section
StateName [initial] [final]
...

# Transitions section
OriginState Symbol DestinationState
...
```

- The first section defines the states. Each line contains a state name, optionally followed by keywords "initial" and/or "final".
- The second section defines the transitions. Each line contains the origin state, the symbol (a single character), and the destination state.
- Lines beginning with '#' are treated as comments and ignored.

### Test File Format

```
Word ExpectedResult
...
```

- Each line contains a word to test, followed by the expected result (true for acceptance, false for rejection).
- Lines beginning with '#' are treated as comments and ignored.

## Running the Code

### Step 1: Compile the Java files

Make sure you are in the project root directory (finite-state-machine-implementation) before running these commands:

```bash
# Create a bin directory for compiled classes
mkdir -p bin

# Compile all Java files to the bin directory
javac -d bin src/automaton/*.java
```

### Step 2: Run the program

After compiling, you can run the program using the following commands:

```bash
# To run the Automaton class with an automaton file and test words
java -cp bin automaton.Automaton src/data/character_automaton.txt [word1 word2 ...]

# To run the TestAutomaton class with an automaton file and a test file
java -cp bin automaton.TestAutomaton src/data/character_automaton.txt src/data/test_cases.txt
```

Note: Make sure to use the correct path to your data files. If the data files are in a src/data directory, use src/data/filename.txt as shown above. If they're directly in a data directory at the project root, use data/filename.txt instead.

## Example

The project includes an example automaton `character_automaton.txt` that describes a character's behavior from TP1, with actions:
And a `test_cases.txt` file with various test sequences to verify the automaton's behavior.