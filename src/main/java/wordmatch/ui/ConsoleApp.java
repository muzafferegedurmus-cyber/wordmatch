package wordmatch.ui;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import wordmatch.domain.*;
import wordmatch.persistence.*;
import wordmatch.service.*;

/** Console interface sharing the GameSession model with Swing. */
public class ConsoleApp {
    private final PrintWriter output;
    private final AppSettings settings;
    private final WordRepository repository;
    private final GameController controller;
    private final BlockingQueue<String> input = new LinkedBlockingQueue<>();
    private volatile boolean inputFinished;
    private volatile IOException inputFailure;

    public ConsoleApp(Reader source, PrintWriter output, AppSettings settings, WordRepository repository) {
        this.output = Objects.requireNonNull(output);
        this.settings = Objects.requireNonNull(settings);
        this.repository = Objects.requireNonNull(repository);
        controller = new GameController(repository);
        // Let the main loop check expiry while readLine waits for user input.
        Thread reader = new Thread(() -> {
            BufferedReader lines = new BufferedReader(source);
            try {
                String line;
                while ((line = lines.readLine()) != null) input.add(line);
            } catch (IOException e) { inputFailure = e; }
            finally { inputFinished = true; }
        }, "wordmatch-console-input");
        reader.setDaemon(true);
        reader.start();
    }
    private String t(String key, Object... values) { return I18n.text(settings.getInterfaceLanguage(), key, values); }
    private void say(String value) { output.println(value); output.flush(); }
    /** Return null on EOF or expiry. Timed polling lets the round end without an Enter press. */
    private String read(long deadlineNanos) {
        try {
            while (true) {
                if (inputFinished && input.isEmpty()) {
                    if (inputFailure != null) say(t("inputError"));
                    return null;
                }
                long remaining = deadlineNanos == Long.MAX_VALUE ? TimeUnit.MILLISECONDS.toNanos(100) : deadlineNanos - System.nanoTime();
                if (remaining <= 0) return null;
                String line = input.poll(Math.min(remaining, TimeUnit.MILLISECONDS.toNanos(100)), TimeUnit.NANOSECONDS);
                if (line != null) return line.strip();
            }
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); return null; }
    }
    private int choice(int minimum, int maximum) {
        while (true) {
            String value = read(Long.MAX_VALUE);
            if (value == null) return -1;
            try {
                int number = Integer.parseInt(value);
                if (number >= minimum && number <= maximum) return number;
            } catch (NumberFormatException ignored) { }
            say(t("numberRange", minimum, maximum));
        }
    }
    public void run() {
        say("WordMatch");
        while (true) {
            say("\n1. " + t("play") + "  2. " + t("settings") + "  3. " + t("how") + "  4. " + t("vocabulary") + "  5. " + t("exit"));
            int menu = choice(1, 5);
            if (menu == -1 || menu == 5) return;
            switch (menu) {
                case 1 -> setupAndPlay();
                case 2 -> changeLanguage();
                case 3 -> say(t("rules", GameRules.CORRECT_POINTS, GameRules.WRONG_POINTS));
                case 4 -> showVocabulary();
                default -> throw new IllegalStateException("Validated menu choice expected");
            }
        }
    }
    private Language selectLanguage(String title) {
        say(title);
        Language[] languages = Language.values();
        for (int i = 0; i < languages.length; i++) say((i + 1) + ". " + languages[i].nativeName);
        int choice = choice(1, languages.length);
        return choice == -1 ? null : languages[choice - 1];
    }
    private void changeLanguage() {
        Language language = selectLanguage(t("interface"));
        if (language != null) { settings.setInterfaceLanguage(language); saveSettings(); }
    }
    private void saveSettings() {
        try { settings.save(); }
        catch (IOException e) { say(t("saveError")); }
    }
    private void setupAndPlay() {
        Language known = selectLanguage(t("known"));
        if (known == null) return;
        Language practice = selectLanguage(t("practice"));
        if (practice == null) return;
        if (known == practice) { say(t("different")); return; }
        GameRules.Difficulty[] difficulties = GameRules.Difficulty.values();
        for (int i = 0; i < difficulties.length; i++) {
            var d = difficulties[i]; say((i + 1) + ". " + t(d.labelKey) + " - " + t("levelInfo", d.pairs, d.seconds));
        }
        int level = choice(1, difficulties.length);
        if (level == -1) return;
        say("1. " + t("randomOrder") + "  2. " + t("alphabeticalOrder"));
        int order = choice(1, 2);
        if (order == -1) return;
        // Select one of two implementations of the same contract at runtime.
        WordOrder policy = order == 1 ? new ShuffledWordOrder() : new AlphabeticalWordOrder();
        controller.setWordOrder(policy);
        settings.setKnownLanguage(known); settings.setPracticeLanguage(practice); settings.setDifficulty(difficulties[level - 1]);
        saveSettings();
        boolean again;
        do {
            playRound();
            say("1. " + t("again") + "  2. " + t("menu"));
            again = choice(1, 2) == 1;
        } while (again);
    }
    private void playRound() {
        GameSession session = controller.startRound(settings.getDifficulty(), settings.getKnownLanguage(), settings.getPracticeLanguage());
        List<Word> left = controller.column(session, settings.getKnownLanguage());
        List<Word> right = controller.column(session, settings.getPracticeLanguage());
        long start = System.nanoTime();
        long deadline = start + TimeUnit.SECONDS.toNanos(session.difficulty().seconds);
        while (!session.finished()) {
            updateTime(session, start);
            if (session.finished()) break;
            say("\n" + t("score") + ": " + session.score() + " | " + t("time") + ": " + session.secondsRemaining());
            say(settings.getKnownLanguage().nativeName + "  <->  " + settings.getPracticeLanguage().nativeName);
            for (int i = 0; i < left.size(); i++) {
                say(String.format("%d. %-28s  %d. %s", i + 1, display(left.get(i), settings.getKnownLanguage(), session),
                        i + 1, display(right.get(i), settings.getPracticeLanguage(), session)));
            }
            say(t("consoleMatch"));
            String answer = read(deadline);
            updateTime(session, start);
            if (session.finished()) break;
            if (answer == null || answer.equalsIgnoreCase("q")) { session.abandon(); break; }
            try {
                int[] selected = parseMatch(answer, left.size());
                GameSession.Attempt attempt = session.attempt(left.get(selected[0]).id(), right.get(selected[1]).id());
                switch (attempt) {
                    case CORRECT -> say(t("correct", GameRules.CORRECT_POINTS));
                    case WRONG -> say(t("wrong", GameRules.WRONG_POINTS));
                    case IGNORED -> say(t("alreadyMatched"));
                }
                session.finishFeedback();
            } catch (InvalidGameException e) { say(t("invalidMatch")); }
        }
        say(t("result") + ": " + t(switch (session.endReason()) {
            case COMPLETED -> "completed"; case TIME_UP -> "timeUp"; case ABANDONED -> "abandoned";
        }));
        say(t("totalScore") + ": " + session.score() + " | " + t("correctCount") + ": " + session.correct()
                + "/" + session.words().size() + " | " + t("wrongCount") + ": " + session.mistakes());
    }
    private String display(Word word, Language language, GameSession session) {
        return (session.isMatched(word.id()) ? "[OK] " : "") + word.text(language);
    }
    private void updateTime(GameSession session, long start) {
        int elapsed = (int) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start);
        int targetRemaining = Math.max(0, session.difficulty().seconds - elapsed);
        while (!session.finished() && session.secondsRemaining() > targetRemaining) session.tick();
    }
    public static int[] parseMatch(String value, int pairCount) {
        String[] parts = value.strip().split("\\s+");
        if (parts.length != 2) throw new InvalidGameException("İki kart numarası girilmelidir.");
        try {
            int a = Integer.parseInt(parts[0]), b = Integer.parseInt(parts[1]);
            if (a < 1 || b < 1 || a > pairCount || b > pairCount) throw new NumberFormatException();
            return new int[]{a - 1, b - 1};
        } catch (NumberFormatException e) { throw new InvalidGameException("Kart numarası aralık dışında."); }
    }
    private void showVocabulary() {
        Language language = selectLanguage(t("known"));
        if (language == null) return;
        WordOrder alphabetical = new AlphabeticalWordOrder();
        for (Word word : alphabetical.arrange(repository.allWords(), language, new Random())) {
            say(word.text(language) + " | " + word.category() + " | " + t(word.difficulty().labelKey));
        }
    }
}
