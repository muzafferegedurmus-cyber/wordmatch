# Problem and design decisions

## Problem

A language learner needs a short vocabulary practice session with immediate feedback. WordMatch provides timed matching rounds between two different languages. The user can play in a desktop window or terminal, and preferences survive application restarts.

## Why Strategy?

Standard play requires shuffled columns. A learner using the console may prefer alphabetical study order to reduce the search effort. Instead of spreading ordering conditionals throughout interfaces and the model, ordering is expressed through one contract:

```java
WordOrder policy = order == 1
        ? new ShuffledWordOrder()
        : new AlphabeticalWordOrder();
controller.setWordOrder(policy);
```

`GameController.column` delegates through that contract:

```java
return wordOrder.arrange(session.words(), language, random);
```

`WordOrder` is the Strategy interface, the two implementations are Concrete Strategies, and `GameController` is the Context. The actual object's type determines the invoked `arrange` implementation at runtime. Scoring, timing and match identity do not depend on the ordering strategy. Swing continues to shuffle each column independently.

## Responsibilities and relationships

- `WordRepository` contains at least 75 immutable `Word` values loaded from UTF-8 data; the UML models this ownership as composition. The same word can be selected for multiple rounds.
- `GameSession` refers to selected words from the repository, modeled as aggregation. It encapsulates score, mistakes, remaining time and the end reason. The diagram summarizes the range as 4–8; the valid counts are 4, 6 and 8.
- `GameController` uses one repository and one ordering strategy; it creates rounds and arranges columns.
- `WordMatchApp` and `ConsoleApp` share domain and service code. This is not claimed to be a complete MVC implementation: much Swing event handling remains in the window class. Strategy is the explicitly demonstrated pattern.
- `WordMatchApp` manages the lifecycle of its sound service and controller. Closing the window stops timers, closes audio resources and shuts down its settings writer.
- `AppSettings` exposes controlled getters/setters. Language fields may temporarily match while a form is being filled in; `GameController` enforces different languages when a round starts.

## Main decisions

1. Preserve the existing Swing game and move it into packages rather than rewriting it. Menus, appearance, sound, localization and core gameplay remain available.
2. Use Java 21 `record Word` as an immutable value: its constructor validates data and defensively copies the translation list.
3. Match concept IDs, not translated strings. Reject repeated visible translations within a round's columns.
4. Return unmodifiable word lists and keep mutable preference fields private.
5. Use `javax.swing.Timer` for Swing and `System.nanoTime` with timed queue reads for the console, so a console round can end without an Enter press.
6. Keep disk writes off the Swing event dispatch thread. Each queued write receives a separate `Properties` snapshot that is not subsequently modified by the UI. `Properties` itself is mutable; the separation is maintained by this usage pattern.
7. Pin Maven through the Wrapper. Keep headless JUnit tests separate from the optional real-window regression program.

## Known limitations

Vocabulary is manually curated; tests cannot establish translation meaning. Scores are not persisted. Preference storage is single-user; concurrent application instances writing the same file use last-writer-wins behavior. Console mode does not use theme or audio settings. Alphabetical study mode intentionally differs from standard shuffled play. Missing audio hardware results in silent operation. The first Maven build needs internet access; the packaged game does not. Swing timers can be delayed when the event thread is busy and are not a real-time scheduling guarantee.

Diagram: [SVG](class-diagram.svg), [PlantUML source](class-diagram.puml). It summarizes important relationships and omits internal drawing classes and some helper methods for readability.
