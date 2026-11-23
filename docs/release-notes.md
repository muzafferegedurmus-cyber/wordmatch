# WordMatch 1.0.1 release notes

Release version: **1.0.1**. The GitHub release record identifies the exact tagged commit. The repository is public and anonymous read access is verified. Publication does not constitute Teams submission.

## Implemented features

- Swing main menu, setup, game, preferences and results screens.
- Five languages, 75 vocabulary concepts and three difficulty levels; +10 for correct matches and -5 for wrong attempts.
- Soft selection audio, light/dark themes, accent colors and persistent preferences.
- Console interface sharing the same model, with random and alphabetical ordering strategies.
- Java 21 Maven build, JUnit tests, UML and English evaluation documentation.

## Limitations and pending delivery

There is no score history. Initial Maven downloads require internet access. Console mode does not use themes or sound. Translation meaning requires human review. The iPad web version is separate from this Java submission. The public repository and hosted backlog are available. English documentation was merged through pull request 4. No instructor invitation is needed for public read access. Teams submission remains the student's final step. The pre-development repository-sharing requirement was not met retrospectively; this timing must be discussed with the instructor.


## Verification and changes since 1.0.0

Re-audited the assignment on 23 November 2026. A fresh GitHub checkout passed all 16 JUnit tests and 5,778 Swing regression checks with JDK 21. Version 1.0.1 updates delivery documentation and the Maven version; gameplay source is unchanged. The exact release commit is rebuilt with `mvnw.cmd clean verify` before tagging. Public access removes the need for an instructor account invitation.
