import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;

/** words.tsv: id, difficulty, category, TR, EN, IT, FR, DE (sekme ile ayrılmış). */
public class WordRepository {
    public record Word(String id, GameRules.Difficulty difficulty, String category, List<String> translations) {
        public String text(Language language) { return translations.get(language.ordinal()); }
    }
    private final List<Word> words = new ArrayList<>();
    public WordRepository() throws IOException {
        InputStream stream = WordRepository.class.getResourceAsStream("/data/words.tsv");
        if (stream == null) throw new IOException("data/words.tsv is missing from the classpath.");
        Set<String> ids = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] columns = line.split("\t", -1);
                if (columns.length != 8 || Arrays.stream(columns).anyMatch(String::isBlank)) throw new IOException("Invalid word row: " + number);
                if (!ids.add(columns[0])) throw new IOException("Duplicate word id: " + columns[0]);
                try { words.add(new Word(columns[0], GameRules.Difficulty.valueOf(columns[1]), columns[2], List.of(Arrays.copyOfRange(columns, 3, 8)))); }
                catch (IllegalArgumentException e) { throw new IOException("Invalid difficulty at row " + number, e); }
            }
        }
        for (GameRules.Difficulty difficulty : GameRules.Difficulty.values()) {
            if (count(difficulty) < 25) throw new IOException("At least 25 words are required for " + difficulty);
        }
    }
    public long count(GameRules.Difficulty difficulty) { return words.stream().filter(w -> w.difficulty() == difficulty).count(); }
    private String normalized(String word) { return Normalizer.normalize(word.strip(), Normalizer.Form.NFKC).toLowerCase(Locale.ROOT); }
    public List<Word> choose(GameRules.Difficulty difficulty, Language left, Language right, Random random) {
        if (left == right) throw new IllegalArgumentException("Choose two different languages.");
        List<Word> pool = new ArrayList<>(words.stream().filter(w -> w.difficulty() == difficulty).toList());
        Collections.shuffle(pool, random);
        Set<String> usedLeft = new HashSet<>(), usedRight = new HashSet<>();
        List<Word> chosen = new ArrayList<>();
        for (Word word : pool) {
            String a = normalized(word.text(left)), b = normalized(word.text(right));
            if (usedLeft.contains(a) || usedRight.contains(b)) continue;
            usedLeft.add(a); usedRight.add(b); chosen.add(word);
            if (chosen.size() == difficulty.pairs) return List.copyOf(chosen);
        }
        throw new IllegalStateException("Insufficient unambiguous words for " + difficulty);
    }
}
