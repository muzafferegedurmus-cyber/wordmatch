# Final acceptance checklist evidence

Source: page 5 of the supplied assignment. This table distinguishes software verification from external delivery. It is not a claim of instructor acceptance.

| Item | Evidence / status |
|---|---|
| Instructor can access the repository | **Available.** The repository is public; anonymous access was verified. No GitHub username or invitation is needed for instructor read access. |
| Clean checkout builds and tests pass | Rechecked on 23 November 2026 using a fresh GitHub clone, JDK 21 and `mvnw.cmd clean verify`: 16 JUnit tests passed. The Swing regression program passed 5,778 checks. Release 1.0.1 changes only delivery documentation and the Maven version; its exact commit is rebuilt before tagging. |
| Encapsulation, abstraction, inheritance, runtime polymorphism | Private mutable fields and validated constructors; WordOrder with ShuffledWordOrder and AlphabeticalWordOrder; GameController invokes the selected implementation at runtime. |
| Design pattern solves an identified problem | Strategy changes column ordering without changing the client or scoring logic. README, design.md and UML identify the participating classes. |
| Data saved/restored and expected failures handled | AppSettings loads/saves preferences; WordRepository reads UTF-8 data; validation and custom InvalidGameException; persistence and invalid-input JUnit tests. |
| UML matches the code | Editable class-diagram.puml and viewable class-diagram.svg describe the main packages, types, methods and multiplicities; helper UI details are explicitly omitted. |
| Issues, meaningful commits, branch/merge evidence and final tag | Hosted issues 1-3, preserved original commits, merged PR 4, and the release-preparation PR. The v1.0.1 tag/release records the final code version; verify it exists via the release link. |
| README explains build/run/test/evaluation | English README includes prerequisites, wrapper commands, console/Swing usage, English language selection, pattern, customization, limitations and contributor disclosure. |

## External steps that cannot be substituted by code

- Public read access is verified; the instructor does not need an invitation.
- Submit the repository URL through the course's Teams assignment; this has not been done by the assistant.
- Explain that repository sharing occurred after development, contrary to the earlier timing requirement on page 3. No history has been backdated.
- The student must understand the implementation and follow the course's AI-use policy. AI assistance is disclosed in README.
