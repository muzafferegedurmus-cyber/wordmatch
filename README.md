# WordMatch - Prog 2 Java OOP Project

**Student:** Muzaffer ege durmus

**Technology:** Java 21, Maven, Swing and console interfaces

**License:** MIT ([LICENSE](LICENSE))

**Submission status:** The local implementation and documentation are available. An online repository, instructor access, hosted issue/PR history and a final tagged release are still pending.

WordMatch helps language learners practise vocabulary by matching words in a familiar language with their equivalents in another language. It provides short timed rounds, immediate feedback and persistent preferences. The vocabulary contains 75 concepts, with 25 at each difficulty level, translated into Turkish, English, Italian, French and German. All 20 ordered pairs of different languages are supported.

## Requirements, build and run

Install JDK 21 or later and check `java -version` and `javac -version`. The Maven Wrapper is included, so a separate Maven installation is not required. The first build needs internet access to download Maven and dependencies. The packaged game runs offline afterwards.

From the project directory in Windows PowerShell:

```powershell
# Clean build, JUnit tests and packaged application
.\mvnw.cmd clean verify

# Swing desktop application
java -jar target/wordmatch.jar

# Required console interface
chcp 65001
java -jar target/wordmatch.jar --console
```

On macOS/Linux, use `./mvnw clean verify` followed by the same `java -jar` commands. If necessary, first run `chmod +x mvnw`. Set `JAVA_HOME` to your JDK directory if Maven cannot find Java. The source files use the standard Maven layout; the former root-level `javac *.java` command is no longer appropriate.

On Windows, `run.bat` builds and launches Swing; `run-console.bat` launches the console. These launchers detect an installed JDK when `JAVA_HOME` is missing. IntelliJ: **Open → pom.xml → Open as Project → Project SDK 21 → Reload Maven**. Run `src/main/java/wordmatch/Main.java`; add `--console` to the run configuration for the console interface. See the [IntelliJ guide](INTELLIJ-GUIDE.md).

## Example usage

The default interface language is Turkish. To evaluate the application in English, choose **Ayarlar (Settings)** and set **Arayüz dili (Interface language)** to **English**. The console equivalent is menu option `2`, then language option `2`. This preference is saved for subsequent launches. The learning language pair remains a separate setting.

Swing: **Play → Türkçe → English → Easy → Start round**. Select one card from each column. Selecting another card in the same column changes the selection. A correct match earns +10 points; a wrong attempt costs 5 points. Scores may be negative. Matched cards are disabled; incorrect cards become available after 650 ms. The round ends when all pairs are matched or time runs out. Returning to the menu stops the timers.

| Difficulty | Pairs | Time |
|---|---:|---:|
| Easy | 4 | 60 seconds |
| Medium | 6 | 45 seconds |
| Hard | 8 | 35 seconds |

Console interaction after selecting English:

```text
1. Play  2. Settings  3. How to play  4. Vocabulary  5. Exit
```

Choose `1`, then select a known language, a practice language and a difficulty from their numbered lists. Choose `1` for random ordering or `2` for alphabetical study ordering. Enter `1 3` to attempt a match between the first left card and third right card, or `q` to leave the round. Card positions vary between rounds. A round times out even if Enter is never pressed. Menu option `4` lists the vocabulary alphabetically in a selected language. Theme and sound settings apply to Swing only.

## Project layout and customization

```text
src/main/java/wordmatch/
  Main.java                 Entry point: --console or Swing
  domain/                   Word, GameSession, Language, GameRules, InvalidGameException
  service/                  GameController, WordOrder and its two strategies
  persistence/              WordRepository, AppSettings
  ui/                       WordMatchApp, ConsoleApp, I18n, SoundEffects
src/main/resources/data/words.tsv
src/test/java/wordmatch/     JUnit tests and a separate Swing regression program
docs/                       UML, user stories, design and submission checklist
```

Add vocabulary to `src/main/resources/data/words.tsv` using UTF-8 and eight tab-separated columns: `id, difficulty, category, TR, EN, IT, FR, DE`. IDs must be unique; difficulty must be `EASY`, `MEDIUM` or `HARD`. Keep at least 25 valid entries at each level. Scoring, time limits and pair counts are defined in `domain/GameRules.java`. Rebuild after changing code or vocabulary.

## Object-oriented design and pattern

**Strategy** separates word ordering from scoring and round management. `WordOrder` defines the contract. `ShuffledWordOrder` shuffles a column; `AlphabeticalWordOrder` sorts using a language-specific `Collator`. `GameController` calls `arrange` through the interface. The console user selects the concrete strategy at runtime. Swing retains independent shuffling for each column.

- **Encapsulation:** mutable state is private; constructors validate domain objects and setters validate preferences. `Word` defensively copies translations, and exposed word lists are unmodifiable.
- **Abstraction, inheritance and polymorphism:** two concrete implementations override `WordOrder.arrange`; the controller uses the common interface. The Swing window also extends `JFrame`; the custom exception extends `IllegalArgumentException`.
- **Relationships:** the repository contains at least 75 words; a round refers to 4, 6 or 8 of them. The controller uses a repository and an ordering strategy. UML includes multiplicities.
- **Collections, generics and sorting:** `List<Word>`, `Set<String>`, `ArrayList<Word>` and `Comparator.comparing` are used.
- **Custom exception:** `InvalidGameException` represents domain-rule violations, including identical languages, invalid card indices, missing translations and invalid rounds.
- **Persistence:** UTF-8 vocabulary is loaded from a resource; preferences are saved to and restored from `.wordmatch/settings.properties` in the user's home directory. File readers/writers use try-with-resources. Save failures are reported. Swing disk writes and audio work run outside the event dispatch thread.

[UML diagram](docs/class-diagram.svg) · [Editable UML](docs/class-diagram.puml) · [Design rationale](docs/design.md) · [User stories](docs/user-stories.md) · [Assignment checklist](docs/assignment-checklist.md)

## Verification

`mvnw clean verify` runs the JUnit suite. Reports are written to `target/surefire-reports`. The 16 JUnit tests cover language pairs and difficulty levels, negative scoring, rapid repeated attempts, end conditions, strategy changes, preference persistence and console input. Tests use temporary directories rather than personal preferences.

An additional regression program opens real Swing windows in a graphical environment:

```powershell
.\run.ps1 -UiTest
```

It checks immediate theme/language updates, responsiveness during delayed disk writes, correct/incorrect matching and timer shutdown. It is separate from the normal Maven/JUnit suite, which does not require a graphical display.

## Submission and known limitations

The assignment deadline is **29 November 2026**. Although earlier pages describe testing as optional, the final submission checklist requires JUnit, so JUnit tests are included.

Online repository setup, instructor access, actual hosted issues/PRs and the final tag/release remain pending. Local history records the original game and the subsequent adaptation. It does not establish that the repository was shared before development began. That timing requirement cannot be satisfied retrospectively and must be discussed with the instructor. See [submission preparation](docs/submission.md).

Only preferences are persisted; there is no score history or database. Vocabulary meaning requires human review. Concurrent instances writing the same preference file use last-writer-wins behavior. Audio falls back to silence when no device is available. The earlier iPad web version is a separate project and is not part of this Java submission.

**Contribution disclosure:** Student: Muzaffer ege durmus. Codex AI assistance was used for implementation, documentation and tests. The student should review and explain the work and follow the course's AI-use policy. The supplied assignment PDF does not state a separate AI policy. The MIT project license does not replace dependency licenses; Maven Wrapper retains its Apache-2.0 notices.
