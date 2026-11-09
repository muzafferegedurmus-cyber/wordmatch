package wordmatch.domain;


/** Central gameplay constants and difficulty settings. */
public final class GameRules {
    private GameRules() {}
    public static final int CORRECT_POINTS = 10;
    public static final int WRONG_POINTS = -5;
    public static final int FEEDBACK_MILLISECONDS = 650;

    public enum Difficulty {
        EASY(4, 60, "easy"), MEDIUM(6, 45, "medium"), HARD(8, 35, "hard");
        public final int pairs;
        public final int seconds;
        public final String labelKey;
        Difficulty(int pairs, int seconds, String labelKey) {
            this.pairs = pairs;
            this.seconds = seconds;
            this.labelKey = labelKey;
        }
    }
}
