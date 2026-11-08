package wordmatch.domain;

import java.util.List;

/** Değiştirilemeyen bir kelime; beş çeviri aynı kavramı temsil eder. */
public record Word(String id, GameRules.Difficulty difficulty, String category, List<String> translations) {
    public Word {
        if (id == null || id.isBlank() || difficulty == null || category == null || category.isBlank()) {
            throw new InvalidGameException("Kelime kimliği, zorluğu ve kategorisi boş olamaz.");
        }
        if (translations == null || translations.size() != Language.values().length
                || translations.stream().anyMatch(t -> t == null || t.isBlank())) {
            throw new InvalidGameException("Her kelimenin beş dilde karşılığı olmalıdır.");
        }
        translations = List.copyOf(translations);
    }
    public String text(Language language) {
        if (language == null) throw new InvalidGameException("Bir dil seçmelisin.");
        return translations.get(language.ordinal());
    }
}
