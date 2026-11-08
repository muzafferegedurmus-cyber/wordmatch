# Ödev PDF'si ile uygunluk kontrolü

Kaynak: Prog2Homework_project_assignment.pdf, 5 sayfa. Bu belge PDF talimatlarını proje gereksinimi olarak değerlendirir; kullanıcı tarafından ertelenen dış işlemleri yapılmış saymaz.

| Şart / sayfa | Durum ve kanıt |
|---|---|
| Java + Maven/Gradle, konsol zorunlu / 1–2 | pom.xml, Maven Wrapper, Main --console; Swing de korunuyor. |
| Kapsülleme / 2 | AppSettings ve GameSession private alanlar; kontrollü metotlar; kurucu doğrulama. |
| Soyutlama / 2 | WordOrder arayüzü. |
| En az iki somut tip ve çalışma zamanı polimorfizmi / 2 | ShuffledWordOrder, AlphabeticalWordOrder, @Override arrange; GameController ortak WordOrder tipini çağırır. |
| Nesneler arası anlamlı ilişkiler / 2 | Havuz-kelime, tur-kelime, controller-policy ilişkileri; UML çoklukları gösterir. |
| Generics ve sıralama / 2 | List<Word>, Set<String>, Comparator.comparing, Collator. |
| Özel exception / 2 | InvalidGameException; aynı dil, yanlış kart girişleri ve geçersiz domain nesneleri. |
| Kalıcı veri, hata yönetimi / 2 | WordRepository okuma, AppSettings yazma/yükleme, try-with-resources, GUI/CLI hata mesajı. |
| Tasarım deseni / 3 | Strategy; README ve docs/design.md roller ve gerekçe. |
| Problem, UML, kararlar, sınırlamalar / 3 | docs/design.md; class-diagram.svg ve düzenlenebilir .puml. |
| Standart dizinler ve paketler / 3 | src/main/java, src/main/resources, src/test/java; domain/service/persistence/ui paketleri. |
| README ve öğrenci adı / 3 | README: Muzaffer ege durmus, çalıştırma/test komutları ve örnekler. |
| Kullanıcı hikâyeleri / 3 | docs/user-stories.md. |
| Lisans / 4 | Kullanıcının seçtiği MIT, LICENSE. Maven Wrapper kendi lisansını korur. |
| JUnit / 3 ve 5 | src/test/java altında JUnit testleri; mvnw clean verify. PDF 2–3. sayfalarda isteğe bağlı, 5. sayfada zorunlu yazdığı için daha sıkı koşul uygulandı. |
| IDE metadata/build/secrets hariç / 3 | .gitignore; target, build, .idea, .vscode, kullanıcı ayarları, web alt projesi hariç. Paketlenen words.tsv program kaynağıdır, kişisel çalışma verisi değildir. |
| Çevrimiçi depo ve hoca erişimi / 1,3,5 | **Bekliyor:** kullanıcı bunları sonra paylaşacağını söyledi. |
| Geliştirmeden önce hocayla paylaşma / 3 | **Geriye dönük doğrulanamaz:** proje önceden geliştirildi. Hocaya açıklanmalı; yapılmış gibi gösterilmez. |
| Issue backlog / 3,5 | docs/backlog.md yerel taslak. GitHub/GitLab issue kayıtları henüz yok. |
| Anlamlı commit ve branch/PR/MR / 3,5 | Gerçek yerel uyarlama commitleri tutulur. Uzak PR/MR süreci henüz yok; taslak backlog bunun yerine geçtiği iddia edilmez. |
| Final tag ve release / 3–5 | **Bekliyor:** depo, inceleme ve hoca erişimi tamamlanınca nihai sürüm etiketlenecek. docs/release-notes.md yalnızca taslaktır. |

Sonuç: Yerel teknik uyarlama ile çevrimiçi teslim adımları ayrı değerlendirilmelidir. GitHub/GitLab ve final teslim adımları bitmeden “PDF'nin tamamı karşılandı” denemez.
