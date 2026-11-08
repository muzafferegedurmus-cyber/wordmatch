package wordmatch.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.*;
import wordmatch.domain.*;
import wordmatch.persistence.*;

class ConsoleAppTest {
    @TempDir Path directory;
    private String run(String commands) throws Exception {
        StringWriter output = new StringWriter();
        new ConsoleApp(new StringReader(commands), new PrintWriter(output), new AppSettings(directory.resolve("settings.properties")), new WordRepository()).run();
        return output.toString();
    }
    @Test void menuHandlesInvalidInputAndEof() throws Exception {
        String output = run("not-a-number\n0\n5\n");
        assertTrue(output.contains("1 ile 5 arasında"));
        assertTrue(run("").contains("WordMatch"));
    }
    @Test void canStartRoundRejectBadPairAndReturnToMenu() throws Exception {
        String output = run("1\n1\n2\n1\n2\n9 9\nq\n2\n5\n");
        assertTrue(output.contains("Geçerli iki kart")); assertTrue(output.contains("Tur bırakıldı"));
        assertTrue(output.contains("Toplam puan: 0"));
    }
    @Test void sameLanguagesAreRejectedAndVocabularyIsAccessible() throws Exception {
        String output = run("1\n1\n1\n4\n2\n5\n");
        assertTrue(output.contains("İki farklı dil")); assertTrue(output.contains("apple | food"));
    }
    @Test void languageSelectionPersistsAndSwitchesMenusImmediately() throws Exception {
        String output = run("2\n2\n5\n"); assertTrue(output.contains("Vocabulary"));
        assertEquals(Language.EN, new AppSettings(directory.resolve("settings.properties")).getInterfaceLanguage());
    }
    @Test void parsesOnlyValidCardIndices() {
        assertArrayEquals(new int[]{0, 2}, ConsoleApp.parseMatch("1 3", 4));
        for (String value : new String[]{"", "1", "a b", "0 2", "1 5", "1 2 3"}) {
            assertThrows(InvalidGameException.class, () -> ConsoleApp.parseMatch(value, 4));
        }
    }
}
