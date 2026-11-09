# Final acceptance checklist evidence

Source: page 5 of the supplied assignment. This table distinguishes software verification from external delivery. It is not a claim of instructor acceptance.

| Item | Evidence / status |
|---|---|
| Instructor can access the repository | **Pending.** The repository is private. Submitting its URL in Teams does not itself grant access. Obtain the instructor's exact account or approved sharing instructions and verify access. |
| Clean checkout builds and tests pass | Fresh GitHub clone built using JDK 21 and `mvnw.cmd clean verify`; 16 JUnit tests passed. The release is also checked from its exact tagged commit before publication. See the PR validation record. |
| Encapsulation, abstraction, inheritance, runtime polymorphism | Private mutable fields and validated constructors; WordOrder with ShuffledWordOrder and AlphabeticalWordOrder; GameController invokes the selected implementation at runtime. |
| Design pattern solves an identified problem | Strategy changes column ordering without changing the client or scoring logic. README, design.md and UML identify the participating classes. |
| Data saved/restored and expected failures handled | AppSettings loads/saves preferences; WordRepository reads UTF-8 data; validation and custom InvalidGameException; persistence and invalid-input JUnit tests. |
| UML matches the code | Editable class-diagram.puml and viewable class-diagram.svg describe the main packages, types, methods and multiplicities; helper UI details are explicitly omitted. |
| Issues, meaningful commits, branch/merge evidence and final tag | Hosted issues 1-3, preserved original commits, merged PR 4, and the release-preparation PR. The v1.0.0 tag/release records the final code version; verify it exists via the release link. |
| README explains build/run/test/evaluation | English README includes prerequisites, wrapper commands, console/Swing usage, English language selection, pattern, customization, limitations and contributor disclosure. |

## External steps that cannot be substituted by code

- Confirm instructor access to the private repository.
- Submit the repository URL through the course's Teams assignment; this has not been done by the assistant.
- Explain that repository sharing occurred after development, contrary to the earlier timing requirement on page 3. No history has been backdated.
- The student must understand the implementation and follow the course's AI-use policy. AI assistance is disclosed in README.
