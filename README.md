# WordMatch — Prog 2 Java OOP projesi

**Öğrenci:** Muzaffer ege durmus  
**Teknoloji:** Java 21, Maven, Swing ve konsol  
**Lisans:** MIT ([LICENSE](LICENSE))  
**Durum:** Yerel kod uyarlaması; GitHub/GitLab deposu, hocanın erişimi ve nihai teslim sürümü henüz tamamlanmadı.

WordMatch, yabancı dil öğrenen kişinin bildiği dildeki sözcükleri başka bir dildeki karşılıklarıyla eşleştirmesini sağlar. Kısa turlar, anında geri bildirim ve kalıcı tercihler sunar. Türkçe, İngilizce, İtalyanca, Fransızca ve Almanca arasında 20 yönlü dil çifti ve her seviyede 25 olmak üzere toplam 75 kelime vardır.

## Kurulum, derleme, test ve çalıştırma

JDK 21 veya daha yenisi gerekir. `java -version` ve `javac -version` ile kontrol et. Maven Wrapper depoda bulunur; ayrıca Maven kurmak gerekmez. **İlk derleme** Maven ve test bağımlılıklarını indirmek için internet kullanır. Oluşan oyun JAR dosyası daha sonra çevrimdışı çalışır.

Windows PowerShell, proje klasöründe:

```powershell
# Temiz derleme ve bütün JUnit testleri
.\mvnw.cmd clean verify

# Mevcut masaüstü oyunu
java -jar target/wordmatch.jar

# PDF'de zorunlu tutulan konsol arayüzü
chcp 65001
java -jar target/wordmatch.jar --console
```

macOS/Linux: `./mvnw clean verify`, ardından aynı `java -jar` komutları. Gerekirse önce `chmod +x mvnw`. JDK bulunamazsa `JAVA_HOME` değerini JDK klasörüne ayarla veya IntelliJ'nin Project SDK ayarını yap. Eski kök dizindeki `javac *.java` komutu artık kullanılmaz; kaynaklar standart Maven yapısına taşındı.

Windows'ta `run.bat` Swing oyununu, `run-console.bat` konsolu derleyip açar. IntelliJ için **Open → pom.xml → Open as Project → JDK 21 → Maven Reload**. `src/main/java/wordmatch/Main.java` içindeki yeşil Run düğmesi Swing'i açar; Run Configuration → Program arguments alanına `--console` yazmak konsolu açar. Ayrıntılar: [IntelliJ rehberi](INTELLIJ-REHBER.md).

## Örnek kullanım

Swing: **Oyna → Türkçe → English → Kolay → Turu Başlat**. Her sütundan bir kart seç; aynı sütunda seçimini değiştirebilirsin. Doğru eşleşme +10, yanlış deneme −5 puandır. Puan negatif olabilir. Doğru kartlar kilitlenir; yanlış kartlar 650 ms sonra tekrar seçilebilir. Bütün çiftler eşleşince veya süre bitince sonuç gösterilir. Menüye dönmek zamanlayıcıları durdurur.

| Zorluk | Çift | Süre |
|---|---:|---:|
| Kolay | 4 | 60 saniye |
| Orta | 6 | 45 saniye |
| Zor | 8 | 35 saniye |

Konsol örneği:

```text
1. Oyna  2. Ayarlar  3. Nasıl Oynanır?  4. Kelime listesi  5. Çıkış
1
Bildiğim dil: 1 (Türkçe)
Pratik yapacağım dil: 2 (English)
Zorluk: 1 (Kolay)
Sıra: 1 (rastgele) veya 2 (alfabetik çalışma sırası)
1 3
q
```

Son iki giriş, sol sütundaki 1. ve sağ sütundaki 3. kartı eşleştirmeyi denemek ve turdan çıkmak içindir. Çiftlerin konumu her tur değişir. Konsolda süre bitince Enter beklenmeden sonuç gösterilir. Menüde 4, seçilen dilde alfabetik kelime listesini gösterir. Konsol ayarlarından arayüz dili değişir; tema ve ses Swing'e özgüdür.

## Kaynak düzeni

```text
src/main/java/wordmatch/
  Main.java                 Başlatıcı; --console veya Swing
  domain/                   Word, GameSession, Language, GameRules, InvalidGameException
  service/                  GameController, WordOrder ve iki stratejisi
  persistence/              WordRepository, AppSettings
  ui/                       WordMatchApp, ConsoleApp, I18n, SoundEffects
src/main/resources/data/words.tsv
src/test/java/wordmatch/     JUnit testleri ve isteğe bağlı Swing kontrolü
docs/                       UML, kullanıcı hikâyeleri, tasarım, kontrol listesi
```

Yeni kelimeler `src/main/resources/data/words.tsv` dosyasına UTF-8 ve sekmeyle ayrılan sekiz sütunla eklenir: `id, difficulty, category, TR, EN, IT, FR, DE`. Kimlik benzersizdir; zorluk `EASY`, `MEDIUM` veya `HARD` olmalıdır. Kurallar `domain/GameRules.java` içindedir. Veriyi veya kodu değiştirdikten sonra yeniden derle.

## OOP ve tasarım deseni

**Strategy**: `WordOrder` ortak sözleşmedir. `ShuffledWordOrder` sütunu karıştırır; `AlphabeticalWordOrder` dilin karşılaştırıcısıyla alfabetik sıralar. `GameController` somut türü bilmeden `arrange` çağırır. Konsol oyuncusu politika seçerek çalışma zamanındaki davranışı değiştirir. Böylece puanlama ve tur mantığını değiştirmeden standart oyun ile alfabetik çalışma sırası sağlanır. Swing varsayılan olarak her iki sütunu bağımsız karıştırır.

- **Kapsülleme:** durum alanları private; geçerli nesneler kurucularda oluşturulur, ayarlar doğrulayan setter metotlarıyla değişir. `Word` kaydı ve dışarı dönen listeler değiştirilemez.
- **Soyutlama / kalıtım / çok biçimlilik:** `WordOrder` arayüzünün iki somut uygulamasında `@Override arrange` vardır. İstemci ortak arayüz tipini kullanır. Swing penceresi ayrıca `JFrame` sınıfından kalıtım alır.
- **İlişkiler:** bir havuz 75 veya daha fazla kelime içerir; bir tur 4/6/8 kelimeyi paylaşır. Controller bir depo ve bir sıralama politikası kullanır.
- **Koleksiyonlar / generics / sıralama:** `List<Word>`, `Set<String>`, `ArrayList<Word>` ve `Comparator.comparing` kullanılır.
- **Özel hata:** `InvalidGameException`; aynı dil çifti, geçersiz kart numarası, eksik çeviri veya geçersiz tur nesnesi gibi alan hatalarını temsil eder.
- **Kalıcılık:** UTF-8 kelime havuzu okunur; tercihler kullanıcı klasöründeki `.wordmatch/settings.properties` dosyasına yazılıp tekrar yüklenir. Dosya okuma/yazmada try-with-resources vardır. Kaydetme hatası kullanıcıya bildirilir. GUI'de disk ve ses işlemleri EDT dışında yürür.

[UML şeması](docs/class-diagram.svg) · [Düzenlenebilir UML](docs/class-diagram.puml) · [Tasarım açıklaması](docs/design.md) · [Kullanıcı hikâyeleri](docs/user-stories.md) · [PDF uygunluk kontrolü](docs/assignment-checklist.md)

## Kontroller

`mvnw clean verify`, JUnit testlerini çalıştırır; test raporları `target/surefire-reports` altında oluşur. Testler 20 dil yönünü üç zorlukta, negatif puanı, hızlı tekrarları, bitişleri, Strategy değişimini, veri kalıcılığını ve konsol girişlerini kontrol eder. Kişisel ayarlar kullanılmaz; geçici klasörler kullanılır.

Grafik ortamda ek Swing regresyon kontrolü:

```powershell
.\run.ps1 -UiTest
```

Bu kontrol gerçek Swing penceresini açar; tema/dil güncelleme, yavaş disk sırasında tepkisellik, doğru/yanlış eşleşme ve zamanlayıcıların durmasını doğrular. Normal Maven/JUnit testi grafik ekran istemez.

## Teslim ve bilinen sınırlar

PDF son teslim tarihi: **29 Kasım 2026**. PDF'nin erken bölümleri testleri isteğe bağlı saysa da son teslim listesi JUnit istediği için JUnit eklendi.

GitHub/GitLab deposu, hoca erişimi, çevrimiçi issue/PR geçmişi ve final tag/release kullanıcının isteğiyle sonraya bırakıldı. Yerel sürüm geçmişi uyarlamadan önceki oyunu ve gerçek uyarlama adımlarını kaydeder; geçmişe dönük commit veya sahte PR oluşturulmaz. “Geliştirmeden önce hocayla paylaşım” şartı geriye dönük tamamlanmış sayılamaz; hocaya açıklanmalıdır. [Teslim hazırlığı](docs/submission.md).

Sonuç geçmişi/veritabanı yoktur; yalnızca tercihler saklanır. Eş anlamlıların anlamsal doğruluğu veri ekleyen kişi tarafından kontrol edilmelidir. Ses aygıtı yoksa oyun sessiz devam eder. iPad için daha önce hazırlanan web sürümü Java ödevi tesliminin parçası değildir; ayrı projedir ve bu uyarlamada değiştirilmedi.

**Katkı açıklaması:** Öğrenci Muzaffer ege durmus. Kod, belge ve test hazırlığında Codex yapay zekâ desteği kullanılmıştır. Öğrenci teslimden önce kodu incelemeli, açıklayabilmeli ve dersin yapay zekâ kullanım politikasına uymalıdır. PDF'de ayrıca açık bir yapay zekâ politikası belirtilmemiştir. MIT proje lisansı, bağımlılıkların lisanslarını değiştirmez; Maven Wrapper kendi Apache-2.0 bildirimleriyle korunur.
