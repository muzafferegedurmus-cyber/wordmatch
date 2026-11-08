import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Properties;

/** Tercihler kullanıcı klasöründeki .wordmatch/settings.properties dosyasında saklanır. */
public class AppSettings {
    public Language interfaceLanguage = Language.TR;
    public Language knownLanguage = Language.TR;
    public Language practiceLanguage = Language.EN;
    public GameRules.Difficulty difficulty = GameRules.Difficulty.EASY;
    public boolean dark = false;
    public String accent = "green";
    public boolean soundEnabled = true;
    private final Path file;

    public AppSettings() { this(Path.of(System.getProperty("user.home"), ".wordmatch", "settings.properties")); }
    public AppSettings(Path file) { this.file = file; load(); }
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
    /** EDT üzerinde alınan tutarlı kopya, arka planda diske yazılır. */
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
