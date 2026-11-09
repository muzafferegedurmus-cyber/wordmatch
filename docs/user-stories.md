# User stories and acceptance criteria

| User story | Acceptance criteria |
|---|---|
| As a language learner, I want to select a familiar language and a practice language. | All 20 ordered pairs among five languages are supported; identical languages are rejected. |
| As a player, I want a short timed round. | Easy uses 4 pairs/60 seconds, medium 6/45 and hard 8/35. |
| As a learner, I want to recognize mistakes and retry. | Wrong attempts cost 5 points; correct matches earn 10; matched cards cannot score twice; negative scores are allowed. |
| As a returning user, I want my preferences restored. | Languages, difficulty, theme, accent and sound preferences are saved and reloaded. |
| As a terminal user, I want to play without a graphical display. | --console provides menus, setup, matching and results; time expires without waiting for Enter. |
| As a learner, I want alphabetical study order. | Selecting the second console strategy sorts each column in its own language; scoring stays the same. |
| As an instructor, I want to build and evaluate the program reproducibly. | A clean checkout builds with mvnw clean verify; the README documents usage and tests. |
| As a student, I want to explain the design. | The UML, Strategy rationale and explanation guide refer to the actual source classes. |
