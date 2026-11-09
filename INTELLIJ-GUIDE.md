# IntelliJ IDEA: WordMatch setup

## Open and run

1. Use **File → Open**, select this project's **pom.xml**, and choose **Open as Project**. Maven determines source roots; do not mark the entire project as a source root manually.
2. In **File → Project Structure → Project SDK**, select JDK 21 or later. For a reproducible development setup, use JDK 21.
3. In the Maven panel, choose **Reload All Maven Projects**. Initial dependency downloads require internet access.
4. Open `src/main/java/wordmatch/Main.java` and click the green **Run** icon beside `main` to launch Swing.
5. For the console, add `--console` under **Run → Edit Configurations → Program arguments**.
6. To evaluate in English, select English in the application's interface-language settings. The application preserves the existing user's language choice.

## Terminal commands

Run these from the project directory:

```powershell
# Clean, compile, run JUnit tests and package the JAR
.\mvnw.cmd clean verify

# Swing interface
java -jar target/wordmatch.jar

# Console interface
chcp 65001
java -jar target/wordmatch.jar --console
```

If Java is not detected, set `JAVA_HOME` to the actual installed JDK directory in the terminal session and add its `bin` directory to `PATH`. The project launchers `run.bat` and `run-console.bat` also attempt to detect the installed JDK.

`clean` removes previous Maven outputs. `verify` runs the build lifecycle through verification, including compilation, tests and packaging. `java -jar` launches the package. `--console` is an application argument interpreted by `Main`.

## Suggested code-reading order

1. `Main`: startup and interface selection.
2. `GameRules`, `Language`, `Word`: rules, enums and immutable vocabulary values.
3. `GameSession`: follow the state changes after a wrong and a correct attempt.
4. `WordRepository`: resource loading and unambiguous word selection.
5. `WordOrder` and its implementations: different runtime behaviors behind one contract.
6. `GameController`: round creation and strategy invocation.
7. `ConsoleApp` and `WordMatchApp`: two interfaces using the same model.
8. `AppSettings`: loading, validation, saving and resource/error handling.

The [oral explanation guide](docs/oral-guide.md) connects concepts to code. The [UML diagram](docs/class-diagram.svg) shows relationships. Explain each operation in terms of its input, state changes and result rather than memorizing unfamiliar lines.
