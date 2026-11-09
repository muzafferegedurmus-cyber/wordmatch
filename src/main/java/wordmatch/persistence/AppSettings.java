package wordmatch.persistence;

import wordmatch.domain.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Properties;

/** Preferences are stored in .wordmatch/settings.properties under the user home directory. */
public class AppSettings {
    private Language interfaceLanguage = Language.TR;
    private Language knownLanguage = Language.TR;
    private Language practiceLanguage = Language.EN;
    private GameRules.Difficulty difficulty = GameRules.Difficulty.EASY;
    private boolean dark = false;
    private String accent = "green";
    private boolean soundEnabled = true;
    private final Path file;

    public AppSettings() { this(Path.of(System.getProperty("user.home"), ".wordmatch", "settings.properties")); }
    public AppSettings(Path file) { this.file = java.util.Objects.requireNonNull(file); load(); }
    public Language getInterfaceLanguage() { return interfaceLanguage; }
    public void setInterfaceLanguage(Language value) { interfaceLanguage = valid(value); }
    public Language getKnownLanguage() { return knownLanguage; }
    public void setKnownLanguage(Language value) { knownLanguage = valid(value); }
    public Language getPracticeLanguage() { return practiceLanguage; }
    public void setPracticeLanguage(Language value) { practiceLanguage = valid(value); }
    public GameRules.Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(GameRules.Difficulty value) {
        if (value == null) throw new InvalidGameException("Zorluk boş olamaz.");
        difficulty = value;
    }
    public boolean isDark() { return dark; }
    public void setDark(boolean value) { dark = value; }
    public String getAccent() { return accent; }
    public void setAccent(String value) {
        if (value == null || !java.util.Set.of("green", "blue", "purple").contains(value)) throw new InvalidGameException("Geçersiz vurgu rengi.");
        accent = value;
    }
    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean value) { soundEnabled = value; }
    private Language valid(Language value) {
        if (value == null) throw new InvalidGameException("Dil boş olamaz.");
        return value;
    }
    private void load() {
        if (!Files.exists(file)) return;
        Properties p = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            p.load(reader);
            interfaceLanguage = enumValue(Language.class, p.getProperty("interfaceLanguage"), Language.TR);
            knownLanguage = enumValue(Language.class, p.getProperty("knownLanguage"), Language.TR);
            practiceLanguage = enumValue(Language.class, p.getProperty("practiceLanguage"), Language.EN);
            if (knownLanguage == practiceLanguage) practiceLanguage = knownLanguage == Language.EN ? Language.TR : Language.EN;
            difficulty = enumValue(GameRules.Difficulty.class, p.getProperty("difficulty"), GameRules.Difficulty.EASY);
            dark = Boolean.parseBoolean(p.getProperty("dark", "false"));
            soundEnabled = Boolean.parseBoolean(p.getProperty("soundEnabled", "true"));
            String savedAccent = p.getProperty("accent", "green");
            if (java.util.Set.of("green", "blue", "purple").contains(savedAccent)) accent = savedAccent;
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Settings could not be loaded: " + e.getMessage());
        }
    }
    private static <T extends Enum<T>> T enumValue(Class<T> type, String value, T fallback) {
        try { return Enum.valueOf(type, value); }
        catch (IllegalArgumentException | NullPointerException e) { return fallback; }
    }
    public void save() throws IOException {
        save(snapshot());
    }
    /** A consistent snapshot is captured on the EDT and written in the background. */
    public Properties snapshot() {
        Properties p = new Properties();
        p.setProperty("interfaceLanguage", interfaceLanguage.name());
        p.setProperty("knownLanguage", knownLanguage.name());
        p.setProperty("practiceLanguage", practiceLanguage.name());
        p.setProperty("difficulty", difficulty.name());
        p.setProperty("dark", Boolean.toString(dark));
        p.setProperty("accent", accent);
        p.setProperty("soundEnabled", Boolean.toString(soundEnabled));
        return p;
    }
    public void save(Properties p) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        Path temporary = Files.createTempFile(file.toAbsolutePath().getParent(), "settings-", ".tmp");
        try {
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) { p.store(writer, "WordMatch"); }
            try { Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException e) { Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temporary); }
    }
}
