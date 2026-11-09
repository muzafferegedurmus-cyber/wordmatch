package wordmatch.ui;

import wordmatch.domain.Language;

import java.text.MessageFormat;
import java.util.*;

/** Translation order: Turkish, English, Italian, French, German. */
public final class I18n {
    private I18n() {}
    private static final Map<String, String[]> TEXT = new HashMap<>();
    static {
        add("vocabulary", "Kelime listesi", "Vocabulary", "Vocabolario", "Vocabulaire", "Wortliste");
        add("numberRange", "{0} ile {1} arasında sayı gir.", "Enter a number from {0} to {1}.", "Inserisci un numero da {0} a {1}.", "Entrez un nombre de {0} à {1}.", "Gib eine Zahl von {0} bis {1} ein.");
        add("randomOrder", "Rastgele sıra (standart)", "Random order (standard)", "Ordine casuale (standard)", "Ordre aléatoire (standard)", "Zufällige Reihenfolge (Standard)");
        add("alphabeticalOrder", "Alfabetik sıra (çalışma)", "Alphabetical order (study)", "Ordine alfabetico (studio)", "Ordre alphabétique (étude)", "Alphabetische Reihenfolge (Übung)");
        add("consoleMatch", "Sol ve sağ kart numarası gir (örnek: 1 3). Menü: q", "Enter left and right card numbers (example: 1 3). Menu: q", "Inserisci i numeri sinistro e destro (esempio: 1 3). Menu: q", "Entrez les numéros gauche et droit (exemple : 1 3). Menu : q", "Gib linke und rechte Kartennummer ein (Beispiel: 1 3). Menü: q");
        add("invalidMatch", "Geçerli iki kart numarası girmelisin.", "Enter two valid card numbers.", "Inserisci due numeri validi.", "Entrez deux numéros valides.", "Gib zwei gültige Kartennummern ein.");
        add("alreadyMatched", "Bu kart zaten eşleştirildi.", "This card is already matched.", "Questa carta è già abbinata.", "Cette carte est déjà associée.", "Diese Karte wurde bereits zugeordnet.");
        add("abandoned", "Tur bırakıldı.", "Round abandoned.", "Turno abbandonato.", "Partie abandonnée.", "Runde abgebrochen.");
        add("inputError", "Konsol girişi okunamadı.", "Unable to read console input.", "Impossibile leggere l’input.", "Impossible de lire la saisie.", "Konsoleneingabe konnte nicht gelesen werden.");
        add("sound", "Oyun sesleri", "Game sounds", "Suoni di gioco", "Sons du jeu", "Spielklänge");
        add("soundOn", "Açık", "On", "Attivi", "Activés", "Ein");
        add("soundOff", "Kapalı", "Off", "Disattivati", "Désactivés", "Aus");
        add("welcome", "Kelimeleri eşleştir, yeni dünyalar keşfet.", "Match words, discover new worlds.", "Abbina parole, scopri nuovi mondi.", "Associez les mots, découvrez de nouveaux mondes.", "Verbinde Wörter, entdecke neue Welten.");
        add("tagline", "KÜÇÜK ADIMLAR, BÜYÜK KEŞİFLER", "SMALL STEPS, BIG DISCOVERIES", "PICCOLI PASSI, GRANDI SCOPERTE", "PETITS PAS, GRANDES DÉCOUVERTES", "KLEINE SCHRITTE, GROSSE ENTDECKUNGEN");
        add("play", "Oyna", "Play", "Gioca", "Jouer", "Spielen");
        add("how", "Nasıl Oynanır?", "How to play", "Come si gioca", "Comment jouer", "Spielanleitung");
        add("settings", "Ayarlar", "Settings", "Impostazioni", "Paramètres", "Einstellungen");
        add("exit", "Çıkış", "Exit", "Esci", "Quitter", "Beenden");
        add("menu", "Ana Menü", "Main menu", "Menu principale", "Menu principal", "Hauptmenü");
        add("back", "Geri", "Back", "Indietro", "Retour", "Zurück");
        add("setup", "Bugün hangi dili keşfedelim?", "Which language will you explore today?", "Quale lingua esplorerai oggi?", "Quelle langue allez-vous explorer ?", "Welche Sprache entdeckst du heute?");
        add("known", "Bildiğim dil", "Language I know", "Lingua che conosco", "Langue que je connais", "Sprache, die ich kenne");
        add("practice", "Pratik yapacağım dil", "Language to practice", "Lingua da praticare", "Langue à pratiquer", "Sprache zum Üben");
        add("difficulty", "Zorluk seviyesi", "Difficulty", "Difficoltà", "Difficulté", "Schwierigkeit");
        add("easy", "Kolay", "Easy", "Facile", "Facile", "Leicht");
        add("medium", "Orta", "Medium", "Medio", "Moyen", "Mittel");
        add("hard", "Zor", "Hard", "Difficile", "Difficile", "Schwer");
        add("levelInfo", "{0} çift · {1} saniye", "{0} pairs · {1} seconds", "{0} coppie · {1} secondi", "{0} paires · {1} secondes", "{0} Paare · {1} Sekunden");
        add("different", "İki farklı dil seçmelisin.", "Choose two different languages.", "Scegli due lingue diverse.", "Choisissez deux langues différentes.", "Wähle zwei verschiedene Sprachen.");
        add("start", "Turu Başlat", "Start round", "Inizia il turno", "Commencer la partie", "Runde starten");
        add("interface", "Arayüz dili", "Interface language", "Lingua dell’interfaccia", "Langue de l’interface", "Oberflächensprache");
        add("theme", "Görünüm", "Appearance", "Aspetto", "Apparence", "Darstellung");
        add("light", "Açık", "Light", "Chiaro", "Clair", "Hell");
        add("dark", "Koyu", "Dark", "Scuro", "Sombre", "Dunkel");
        add("accent", "Vurgu rengi", "Accent color", "Colore principale", "Couleur d’accent", "Akzentfarbe");
        add("green", "Yeşil", "Green", "Verde", "Vert", "Grün");
        add("blue", "Mavi", "Blue", "Blu", "Bleu", "Blau");
        add("purple", "Mor", "Purple", "Viola", "Violet", "Violett");
        add("saved", "Değişiklikler anında uygulanır ve kaydedilir.", "Changes are applied and saved instantly.", "Le modifiche vengono applicate e salvate subito.", "Les changements sont appliqués et enregistrés immédiatement.", "Änderungen werden sofort angewendet und gespeichert.");
        add("score", "PUAN", "SCORE", "PUNTI", "SCORE", "PUNKTE");
        add("time", "KALAN SÜRE", "TIME LEFT", "TEMPO RIMASTO", "TEMPS RESTANT", "RESTZEIT");
        add("progress", "EŞLEŞEN", "MATCHED", "ABBINATE", "ASSOCIÉES", "ZUGEORDNET");
        add("prompt", "Her sütundan bir kelime seç.", "Choose one word from each column.", "Scegli una parola da ogni colonna.", "Choisissez un mot dans chaque colonne.", "Wähle ein Wort aus jeder Spalte.");
        add("correct", "Harika eşleşme! +{0}", "Great match! +{0}", "Ottimo abbinamento! +{0}", "Bonne association ! +{0}", "Richtig zugeordnet! +{0}");
        add("wrong", "Tekrar dene. {0} puan", "Try again. {0} points", "Riprova. {0} punti", "Réessayez. {0} points", "Versuch es erneut. {0} Punkte");
        add("completed", "Bütün çiftleri eşleştirdin!", "You matched every pair!", "Hai abbinato tutte le coppie!", "Vous avez associé toutes les paires !", "Du hast alle Paare gefunden!");
        add("timeUp", "Süre doldu!", "Time is up!", "Tempo scaduto!", "Le temps est écoulé !", "Die Zeit ist um!");
        add("result", "Tur Sonucu", "Round results", "Risultati del turno", "Résultats de la partie", "Rundenergebnis");
        add("totalScore", "Toplam puan", "Total score", "Punteggio totale", "Score total", "Gesamtpunktzahl");
        add("correctCount", "Doğru eşleşme", "Correct matches", "Abbinamenti corretti", "Associations correctes", "Richtige Zuordnungen");
        add("wrongCount", "Yanlış deneme", "Wrong attempts", "Tentativi errati", "Tentatives incorrectes", "Fehlversuche");
        add("again", "Tekrar Oyna", "Play again", "Gioca ancora", "Rejouer", "Erneut spielen");
        add("offline", "5 dil · Çevrimdışı · Her gün yeni bir adım", "5 languages · Offline · One step every day", "5 lingue · Offline · Un passo ogni giorno", "5 langues · Hors ligne · Un pas chaque jour", "5 Sprachen · Offline · Jeden Tag ein Schritt");
        add("rules", "Sol ve sağ sütundan aynı anlama gelen kelimeleri seç.\nAynı sütunda başka bir karta basarak seçimini değiştirebilirsin.\nDoğru eşleşme: +{0} puan. Yanlış deneme: {1} puan.\nPuan sıfırın altına düşebilir. Yeşil kartlar tamamlanmıştır.\nKırmızı kartlar kısa süre sonra yeniden seçilebilir.\nTüm çiftler eşleşince veya süre bitince tur sona erer.", "Select words with the same meaning from the left and right columns.\nClick another card in the same column to change your selection.\nCorrect match: +{0} points. Wrong attempt: {1} points.\nYour score can be negative. Green cards are complete.\nRed cards become available again after a short pause.\nThe round ends when all pairs match or time runs out.", "Scegli parole con lo stesso significato dalle due colonne.\nPremi un’altra carta nella stessa colonna per cambiare scelta.\nAbbinamento corretto: +{0} punti. Errore: {1} punti.\nIl punteggio può essere negativo. Le carte verdi sono completate.\nLe carte rosse tornano disponibili dopo una breve pausa.\nIl turno finisce quando abbini tutto o il tempo scade.", "Choisissez les mots de même sens dans les deux colonnes.\nCliquez sur une autre carte de la même colonne pour changer de choix.\nBonne association : +{0} points. Erreur : {1} points.\nLe score peut être négatif. Les cartes vertes sont terminées.\nLes cartes rouges redeviennent disponibles après une courte pause.\nLa partie se termine quand tout est associé ou que le temps est écoulé.", "Wähle Wörter mit gleicher Bedeutung aus beiden Spalten.\nKlicke auf eine andere Karte derselben Spalte, um die Auswahl zu ändern.\nRichtiges Paar: +{0} Punkte. Fehlversuch: {1} Punkte.\nDie Punktzahl kann negativ sein. Grüne Karten sind abgeschlossen.\nRote Karten sind nach einer kurzen Pause wieder verfügbar.\nDie Runde endet, wenn alle Paare gefunden sind oder die Zeit abläuft.");
        add("error", "Bir sorun oluştu", "Something went wrong", "Si è verificato un problema", "Un problème est survenu", "Ein Problem ist aufgetreten");
        add("saveError", "Ayarlar kaydedilemedi. Klasörün yazılabilir olduğunu kontrol et.", "Settings could not be saved. Check that the folder is writable.", "Impossibile salvare. Verifica i permessi della cartella.", "Impossible d’enregistrer. Vérifiez les droits du dossier.", "Speichern fehlgeschlagen. Prüfe die Schreibrechte des Ordners.");
        add("dataError", "Kelime verisi yüklenemedi. README içindeki çalıştırma adımlarını kontrol et.", "Word data could not be loaded. Check the launch steps in README.", "Impossibile caricare le parole. Controlla le istruzioni nel README.", "Impossible de charger les mots. Consultez le README.", "Wortdaten konnten nicht geladen werden. Prüfe die Anleitung im README.");
    }
    private static void add(String key, String... translations) { TEXT.put(key, translations); }
    public static String text(Language language, String key, Object... arguments) {
        String value = TEXT.get(key)[language.ordinal()];
        // Escape natural-language apostrophes for MessageFormat.
        return arguments.length == 0 ? value : MessageFormat.format(value.replace("'", "''"), arguments);
    }
}
