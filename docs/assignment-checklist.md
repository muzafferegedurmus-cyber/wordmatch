# Assignment compliance checklist

Source: Prog2Homework_project_assignment.pdf, five pages. Technical implementation and external submission steps are tracked separately. Pending tasks are not represented as completed.

| Requirement / assignment page | Evidence and status |
|---|---|
| Java, Maven/Gradle and required console / 1–2 | pom.xml, Maven Wrapper and Main --console; Swing is retained. |
| Encapsulation / 2 | Private AppSettings/GameSession fields, controlled methods and constructor validation. |
| Abstraction / 2 | WordOrder interface. |
| Two concrete types and runtime polymorphism / 2 | ShuffledWordOrder and AlphabeticalWordOrder override arrange; GameController invokes the common WordOrder type. |
| Meaningful relationships / 2 | Repository-to-word, session-to-word and controller-to-policy relationships; UML multiplicities. |
| Generics and sorting / 2 | List<Word>, Set<String>, Comparator.comparing and Collator. |
| Custom exception / 2 | InvalidGameException for invalid languages, card input and domain objects. |
| Persistence and failure handling / 2 | WordRepository reading, AppSettings writing/loading, try-with-resources and GUI/CLI error reporting. |
| Design pattern / 3 | Strategy; participating classes and rationale in README and design.md. |
| Problem, UML, decisions and limitations / 3 | design.md, viewable class-diagram.svg and editable class-diagram.puml. |
| Conventional structure and packages / 3 | src/main/java, src/main/resources, src/test/java; domain/service/persistence/ui packages. |
| README and contributor / 3 | Muzaffer ege durmus, prerequisites, build/run/test commands, examples and contribution disclosure. |
| User stories / 3 | user-stories.md. |
| License / 4 | Student-selected MIT license; Maven Wrapper retains its own notices. |
| JUnit / 3 and 5 | Tests under src/test/java; mvnw clean verify. Earlier pages say optional, but page 5 requires JUnit; the stricter requirement is implemented. |
| Exclude IDE output and secrets / 3 | .gitignore excludes target, build, .idea, .vscode, local settings and the separate web project. Packaged words.tsv is application content, not private runtime data. |
| Online repository and instructor access / 1,3,5 | Pending repository setup and the instructor's account/access instructions. |
| Share before development / 3 | Not established retrospectively: development already occurred. This must be disclosed to the instructor. |
| Issue backlog / 3,5 | backlog.md is a local draft; actual hosted issue records are pending. |
| Meaningful commits and branch/PR/MR process / 3,5 | Real local adaptation history exists. Hosted PR/MR review is pending; local notes do not replace it. |
| Final tag and release / 3–5 | Pending final review and access verification. release-notes.md is a draft. |

The complete submission cannot be declared finished until repository access and the required hosted workflow/release steps are verified.
