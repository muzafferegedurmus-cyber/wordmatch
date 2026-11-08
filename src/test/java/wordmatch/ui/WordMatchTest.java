package wordmatch.ui;

import wordmatch.domain.*;
import wordmatch.persistence.*;
import wordmatch.service.*;
import wordmatch.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;

/** Bağımlılık gerektirmeyen model ve gerçek Swing ekran testleri. */
public class WordMatchTest {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    public static void main(String[] args) throws Exception {
        WordRepository repository = new WordRepository();
        Random random = new Random(42);
        for (GameRules.Difficulty difficulty : GameRules.Difficulty.values()) {
            check(repository.count(difficulty) >= 25, "Pool size");
            for (Language left : Language.values()) for (Language right : Language.values()) {
                if (left == right) continue;
                for (int round = 0; round < 30; round++) {
                    var words = repository.choose(difficulty, left, right, random);
                    check(words.size() == difficulty.pairs, "Pair count");
                    check(words.stream().map(w -> w.text(left)).distinct().count() == words.size(), "Unique left");
                    check(words.stream().map(w -> w.text(right)).distinct().count() == words.size(), "Unique right");
                }
            }
            var words = repository.choose(difficulty, Language.TR, Language.EN, random);
            GameSession game = new GameSession(words, difficulty);
            check(game.attempt(words.get(0).id(), words.get(1).id()) == GameSession.Attempt.WRONG, "Wrong attempt");
            check(game.score() == -5 && game.mistakes() == 1, "Negative score");
            check(game.attempt(words.get(0).id(), words.get(1).id()) == GameSession.Attempt.IGNORED, "Rapid clicks ignored");
            game.finishFeedback();
            for (var word : words) {
                check(game.attempt(word.id(), word.id()) == GameSession.Attempt.CORRECT, "Correct attempt");
                game.finishFeedback();
                check(game.attempt(word.id(), word.id()) == GameSession.Attempt.IGNORED, "Matched cards ignored");
            }
            check(game.endReason() == GameSession.EndReason.COMPLETED, "Completion");
            check(game.score() == difficulty.pairs * 10 - 5, "Final score");
            int oldTime = game.secondsRemaining(); game.tick(); check(game.secondsRemaining() == oldTime, "Completed timer stopped");
            GameSession timed = new GameSession(words, difficulty);
            for (int i = 0; i < difficulty.seconds; i++) timed.tick();
            check(timed.endReason() == GameSession.EndReason.TIME_UP && timed.secondsRemaining() == 0, "Time up");
            timed.tick(); check(timed.secondsRemaining() == 0, "No negative timer");
            GameSession next = new GameSession(words, difficulty);
            check(next.score() == 0 && next.secondsRemaining() == difficulty.seconds, "Fresh round");
            next.abandon(); next.tick(); check(next.secondsRemaining() == difficulty.seconds, "Abandoned timer stopped");
        }
        Path folder = Files.createTempDirectory("wordmatch-test-");
        Path file = folder.resolve("settings.properties");
        AppSettings settings = new AppSettings(file);
        settings.setDark(true); settings.setAccent("purple"); settings.setInterfaceLanguage(Language.DE);
        settings.setKnownLanguage(Language.IT); settings.setPracticeLanguage(Language.FR); settings.setDifficulty(GameRules.Difficulty.HARD); settings.save();
        AppSettings loaded = new AppSettings(file);
        check(loaded.isSoundEnabled(), "Sound enabled by default");
        settings.setSoundEnabled(false); settings.save();
        check(!new AppSettings(file).isSoundEnabled(), "Sound preference persists");
        byte[] samples = SoundEffects.selectionSamples();
        int peak = 0;
        for (int i = 0; i < samples.length; i += 2) peak = Math.max(peak, Math.abs((short)((samples[i] & 255) | samples[i+1] << 8)));
        check(peak > 0 && peak < 1500, "Soft low-volume audio waveform");
        check(samples[0] == 0 && samples[1] == 0 && samples[samples.length-1] == 0 && samples[samples.length-2] == 0, "Click-free sound envelope");
        try (SoundEffects audio = new SoundEffects()) {
            System.out.println("Audio output available: " + audio.availableForTest());
            audio.playSelection();
            Thread.sleep(200); // Ses çıkışına gerçek bir kısa oynatma fırsatı ver.
        }
        check(loaded.isDark() && loaded.getAccent().equals("purple") && loaded.getInterfaceLanguage() == Language.DE, "Appearance persistence");
        check(loaded.getKnownLanguage() == Language.IT && loaded.getPracticeLanguage() == Language.FR && loaded.getDifficulty() == GameRules.Difficulty.HARD, "Round persistence");
        for (Language language : Language.values()) {
            Field mapField = I18n.class.getDeclaredField("TEXT"); mapField.setAccessible(true);
            for (Object key : ((Map<?, ?>) mapField.get(null)).keySet()) check(!I18n.text(language, (String)key).isBlank(), "Translation exists");
        }
        if (!GraphicsEnvironment.isHeadless()) {
            verifySettingsStayResponsive(folder.resolve("slow-settings.properties"));
            SwingUtilities.invokeAndWait(() -> {
                WordMatchApp app = new WordMatchApp(new AppSettings(file));
                try {
                    app.setVisible(true);
                    click(app, "settings");
                    Component originalTheme = find(app, "theme");
                    select(app, "interface", Language.TR); select(app, "theme", "light"); select(app, "accent", "green");
                    check(originalTheme == find(app, "theme"), "Settings controls retained during updates");
                    Color lightBackground = app.getContentPane().getBackground();
                    select(app, "theme", "dark");
                    check(!lightBackground.equals(app.getContentPane().getBackground()), "Theme applies immediately");
                    select(app, "interface", Language.EN);
                    check(((AbstractButton)find(app, "menu")).getText().equals("Main menu"), "Language applies immediately");
                    select(app, "interface", Language.TR); select(app, "theme", "light");
                    awaitSave(app);
                    check(!new AppSettings(file).isDark() && new AppSettings(file).getInterfaceLanguage() == Language.TR, "UI saves settings");
                    click(app, "menu"); capture(app, "menu.png"); click(app, "play");
                    select(app, "known", Language.TR); select(app, "practice", Language.TR);
                    check(!find(app, "start").isEnabled(), "Same languages blocked");
                    select(app, "practice", Language.EN); select(app, "difficulty", GameRules.Difficulty.HARD); click(app, "start");
                    GameSession session = (GameSession) field(app, "session");
                    String first = session.words().get(0).id(), second = session.words().get(1).id();
                    click(app, "left-" + first); click(app, "left-" + second); click(app, "right-" + second);
                    check(session.score() == 10, "Selection change works");
                    click(app, "left-" + first); check(session.score() == 10, "Feedback locks UI");
                    fireTimer(app, "feedbackTimer");
                    click(app, "left-" + first); click(app, "right-" + session.words().get(2).id());
                    check(session.score() == 5 && session.mistakes() == 1, "Wrong UI match");
                    fireTimer(app, "feedbackTimer"); capture(app, "game.png");
                    for (var word : session.words()) if (!session.isMatched(word.id())) {
                        click(app, "left-" + word.id()); click(app, "right-" + word.id()); fireTimer(app, "feedbackTimer");
                    }
                    check(find(app, "again") != null && !((javax.swing.Timer)field(app, "countdown")).isRunning(), "Completion stops timer");
                    capture(app, "results.png"); click(app, "again");
                    session = (GameSession)field(app, "session"); check(session.secondsRemaining() == 35 && session.score() == 0, "Replay resets round");
                    for (int i = 0; i < 35; i++) fireTimer(app, "countdown");
                    check(session.endReason() == GameSession.EndReason.TIME_UP && find(app, "again") != null, "Timer reaches results");
                    click(app, "again"); click(app, "menu");
                    check(!((javax.swing.Timer)field(app, "countdown")).isRunning(), "Menu stops countdown");
                    click(app, "settings"); select(app, "theme", "dark"); select(app, "accent", "purple"); capture(app, "settings-dark.png");
                    awaitSave(app);
                    app.dispose();
                    WordMatchApp reopened = new WordMatchApp(new AppSettings(file));
                    try {
                        reopened.setVisible(true); click(reopened, "settings");
                        check(((JComboBox<?>)find(reopened, "theme")).getSelectedItem().equals("dark"), "Reopened UI preserves theme");
                    } finally { reopened.dispose(); }
                } catch (Exception e) { throw new RuntimeException(e); }
                finally { app.dispose(); }
            });
        }
        Files.deleteIfExists(file); Files.deleteIfExists(folder);
        System.out.println("PASS: " + checks + " checks (model, translations, persistence, Swing UI).");
    }
    private static void verifySettingsStayResponsive(Path file) throws Exception {
        var writing = new java.util.concurrent.CountDownLatch(1);
        var release = new java.util.concurrent.CountDownLatch(1);
        AppSettings slowSettings = new AppSettings(file) {
            @Override public void save(Properties snapshot) throws java.io.IOException {
                writing.countDown();
                try { if (!release.await(5, java.util.concurrent.TimeUnit.SECONDS)) throw new java.io.IOException("Test write timeout"); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new java.io.IOException(e); }
                super.save(snapshot);
            }
        };
        WordMatchApp[] window = new WordMatchApp[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                window[0] = new WordMatchApp(slowSettings); window[0].setVisible(true);
                click(window[0], "settings"); select(window[0], "theme", "dark");
            });
            check(writing.await(2, java.util.concurrent.TimeUnit.SECONDS), "Background save started");
            SwingUtilities.invokeAndWait(() -> {
                check(slowSettings.isDark(), "Theme changed while disk blocked");
                select(window[0], "accent", "blue");
                select(window[0], "interface", Language.FR);
                check(((AbstractButton)find(window[0], "menu")).getText().equals("Menu principal"), "UI responsive during blocked write");
                click(window[0], "menu");
                check(find(window[0], "play") != null, "Navigation responsive during blocked write");
            });
            release.countDown();
            awaitSave(window[0]);
            AppSettings result = new AppSettings(file);
            check(result.isDark() && result.getAccent().equals("blue") && result.getInterfaceLanguage() == Language.FR, "Queued saves preserve latest settings");
        } finally {
            release.countDown();
            if (window[0] != null) SwingUtilities.invokeAndWait(window[0]::dispose);
            Files.deleteIfExists(file);
        }
    }
    private static Object field(Object target, String name) throws Exception { Field f = target.getClass().getDeclaredField(name); f.setAccessible(true); return f.get(target); }
    // Yalnızca test diskin tamamlanmasını bekler; uygulama EDT üzerinde beklemez.
    private static void awaitSave(Object app) throws Exception { ((java.util.concurrent.Future<?>)field(app, "latestSave")).get(10, java.util.concurrent.TimeUnit.SECONDS); }
    private static Component find(Container parent, String name) {
        for (Component c : parent.getComponents()) {
            if (name.equals(c.getName())) return c;
            if (c instanceof Container nested) { Component found = find(nested, name); if (found != null) return found; }
        }
        return null;
    }
    private static void click(Container app, String name) { ((AbstractButton)Objects.requireNonNull(find(app, name), name)).doClick(0); }
    @SuppressWarnings("unchecked") private static void select(Container app, String name, Object value) { ((JComboBox<Object>)find(app, name)).setSelectedItem(value); }
    private static void fireTimer(Object app, String name) throws Exception {
        javax.swing.Timer timer = (javax.swing.Timer)field(app, name);
        if (name.equals("feedbackTimer")) timer.stop();
        for (var listener : timer.getActionListeners()) listener.actionPerformed(new java.awt.event.ActionEvent(timer, 0, "test"));
    }
    private static void capture(JFrame app, String name) throws Exception {
        app.validate();
        BufferedImage image = new BufferedImage(app.getWidth(), app.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics(); app.paint(graphics); graphics.dispose();
        Files.createDirectories(Path.of("build", "screenshots"));
        ImageIO.write(image, "png", Path.of("build", "screenshots", name).toFile());
    }
}
