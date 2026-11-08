import java.util.*;

/** Swing'den bağımsız oyun durumu. Bir deneme geri bildirimi bitene kadar kilitlenir. */
public class GameSession {
    public enum Attempt { CORRECT, WRONG, IGNORED }
    public enum EndReason { COMPLETED, TIME_UP, ABANDONED }
    public final List<WordRepository.Word> words;
    public final GameRules.Difficulty difficulty;
    private final Set<String> ids = new HashSet<>(), matched = new HashSet<>();
    private int score, mistakes, secondsRemaining;
    private boolean feedbackPending;
    private EndReason endReason;
    public GameSession(List<WordRepository.Word> words, GameRules.Difficulty difficulty) {
        this.words = List.copyOf(words);
        this.difficulty = difficulty;
        secondsRemaining = difficulty.seconds;
        words.forEach(w -> ids.add(w.id()));
    }
    public Attempt attempt(String leftId, String rightId) {
        if (finished() || feedbackPending || !ids.contains(leftId) || !ids.contains(rightId) || matched.contains(leftId) || matched.contains(rightId)) return Attempt.IGNORED;
        feedbackPending = true;
        if (leftId.equals(rightId)) {
            matched.add(leftId);
            score += GameRules.CORRECT_POINTS;
            if (matched.size() == words.size()) endReason = EndReason.COMPLETED;
            return Attempt.CORRECT;
        }
        score += GameRules.WRONG_POINTS;
        mistakes++;
        return Attempt.WRONG;
    }
    public void finishFeedback() { feedbackPending = false; }
    public void tick() {
        if (finished()) return;
        if (--secondsRemaining <= 0) { secondsRemaining = 0; endReason = EndReason.TIME_UP; }
    }
    public void abandon() { if (!finished()) endReason = EndReason.ABANDONED; }
    public boolean finished() { return endReason != null; }
    public boolean feedbackPending() { return feedbackPending; }
    public boolean isMatched(String id) { return matched.contains(id); }
    public int score() { return score; }
    public int mistakes() { return mistakes; }
    public int correct() { return matched.size(); }
    public int secondsRemaining() { return secondsRemaining; }
    public EndReason endReason() { return endReason; }
}
