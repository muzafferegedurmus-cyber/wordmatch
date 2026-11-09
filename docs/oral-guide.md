# Explaining the code

Use each example alongside the actual source in IntelliJ. For any statement, ask: what information does it take, what does it change, and what result does the user see?

## Entry point

`Main.main` starts the application. With `--console`, it runs `ConsoleApp.run()`. Otherwise, `SwingUtilities.invokeLater` creates the window on Swing's event dispatch thread (EDT). `new WordMatchApp()` constructs it; `setVisible(true)` displays it.

## Rules versus session state

`GameRules.CORRECT_POINTS` defines the reward for a match. `GameSession.score` holds the current round's score. `score += GameRules.CORRECT_POINTS` adds the rule's value to the current state.

## Access and modifiers

- `private int score`: other classes cannot change the field directly; `attempt` applies the rules.
- `public int score()`: exposes the value for reading.
- `static`: belongs to the class rather than one instance.
- `final`: prevents reassignment. It does not make a referenced list's contents immutable; defensive `List.copyOf` copies provide that protection here.

## Constructors, records and enums

The `GameSession` constructor validates count, IDs and difficulty before accepting the words. In `this.difficulty = difficulty`, the left side is the field and the right side is the parameter. The initial time comes from `difficulty.seconds`.

`Word` is a record holding an ID, difficulty, category and translations. Its compact constructor validates data and copies the list. Java supplies accessors and value operations. `Language` and `Difficulty` enums constrain choices to known values.

## Match processing

1. The interface sends the two concept IDs to `attempt`.
2. Finished rounds, pending feedback, invalid IDs or previously matched cards return `IGNORED`.
3. Matching IDs, compared with `equals`, identify translations of the same concept.
4. A correct ID enters the `matched` set and earns 10 points. Matching every word sets `COMPLETED`.
5. A wrong attempt adds -5 points and increments mistakes. `finishFeedback` releases the temporary lock.

## Collections

`List<Word>` preserves ordering and restricts elements to Word values. `Set<String>` keeps unique matched IDs. `ArrayList` and `HashSet` are concrete implementations of those interfaces. `Map<String, String[]>` connects localization keys to translations.

## Interface, overriding and runtime polymorphism

`WordOrder` declares the `arrange` contract. Each `implements WordOrder` class overrides it. With `WordOrder policy = new AlphabeticalWordOrder()`, the declared type is the interface while the runtime object is the alphabetical strategy. Calling `arrange` invokes that object's implementation. A different concrete strategy changes behavior behind the same call. Show `GameController.column` and the selection in `ConsoleApp` to demonstrate actual use.

## Sorting and lambdas

`Comparator.comparing(word -> word.text(language), collator)` selects the translated word as the sort key and uses the chosen language's `Collator`. `result.sort(...)` applies the comparison. The lambda extracts the relevant text; it does not itself start a background thread.

## Exceptions

`InvalidGameException` extends `IllegalArgumentException` and is a custom unchecked exception for invalid game input. The console catches it and reports invalid input. `IOException` describes file/stream failures rather than a game-rule violation.

## Persistence and resources

`WordRepository` reads the bundled TSV resource. `AppSettings` loads/saves a Properties file. A `try (Reader reader = ...)` block closes its reader even after an exception. Saving through a temporary file reduces the risk of leaving a partially written main preference file.

## Responsiveness

Swing component updates happen on the EDT. Disk writes and audio run on executor threads so they do not block interaction. Preference controls are updated in place rather than rebuilding an open combo box's component tree. Errors return to the EDT through `invokeLater`. A regression test deliberately delays disk writes to check responsiveness.

## Tests

`@Test` identifies a JUnit test. `assertEquals(-5, game.score())` compares expected and actual results. `assertThrows` checks the expected failure, while `@TempDir` isolates test preferences from real user files. Passing tests establish the checked scenarios, not the absence of every possible defect.
