package wordmatch.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.io.IOException;
import wordmatch.domain.*;

class AppSettingsTest {
    @TempDir Path directory;
    @Test void savesAndRestoresAllPreferences() throws Exception {
        Path file = directory.resolve("settings.properties");
        AppSettings settings = new AppSettings(file);
        settings.setInterfaceLanguage(Language.IT); settings.setKnownLanguage(Language.FR); settings.setPracticeLanguage(Language.DE);
        settings.setDifficulty(GameRules.Difficulty.HARD); settings.setDark(true); settings.setAccent("purple"); settings.setSoundEnabled(false);
        settings.save(); AppSettings loaded = new AppSettings(file);
        assertEquals(Language.IT, loaded.getInterfaceLanguage()); assertEquals(Language.FR, loaded.getKnownLanguage()); assertEquals(Language.DE, loaded.getPracticeLanguage());
        assertEquals(GameRules.Difficulty.HARD, loaded.getDifficulty()); assertTrue(loaded.isDark()); assertEquals("purple", loaded.getAccent()); assertFalse(loaded.isSoundEnabled());
    }
    @Test void snapshotKeepsOneConsistentVersion() throws Exception {
        Path file = directory.resolve("settings.properties"); AppSettings settings = new AppSettings(file);
        var snapshot = settings.snapshot(); settings.setDark(true); settings.save(snapshot);
        assertFalse(new AppSettings(file).isDark()); settings.save(); assertTrue(new AppSettings(file).isDark());
    }
    @Test void invalidSettingsFallBackAndSettersValidate() throws Exception {
        Path file = directory.resolve("settings.properties");
        Files.writeString(file, "interfaceLanguage=bad\nknownLanguage=TR\npracticeLanguage=TR\naccent=invalid\ndifficulty=bad\n");
        var settings = new AppSettings(file);
        assertEquals(Language.TR, settings.getInterfaceLanguage()); assertEquals(Language.EN, settings.getPracticeLanguage());
        assertEquals("green", settings.getAccent()); assertEquals(GameRules.Difficulty.EASY, settings.getDifficulty());
        assertThrows(InvalidGameException.class, () -> settings.setAccent("invalid"));
        assertThrows(InvalidGameException.class, () -> settings.setKnownLanguage(null));
        assertThrows(InvalidGameException.class, () -> settings.setDifficulty(null));
    }
    @Test void writeFailureIsReportedInsteadOfPretendingToSave() throws Exception {
        Path parent = directory.resolve("not-a-directory"); Files.writeString(parent, "x");
        assertThrows(IOException.class, () -> new AppSettings(parent.resolve("settings.properties")).save());
    }
}
