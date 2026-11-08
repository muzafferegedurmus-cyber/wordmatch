# Hocaya kodu anlatma rehberi

Bu bir ezber metni değil; her örneği IntelliJ'de bulup bir kez kendin takip et. Her satırda: hangi bilgiyi alıyor, ne yapıyor, sonucu ne?

## Program nereden başlıyor?

`Main.main` Java'nın giriş noktasıdır. Argümanlarda `--console` varsa `ConsoleApp.run()` çağrılır. Yoksa `SwingUtilities.invokeLater` ile pencere Swing'in arayüz iş parçacığında oluşturulur. `new WordMatchApp()` nesneyi kurar; `setVisible(true)` görünür yapar.

## Kurallar ve tur neden ayrı?

`GameRules.CORRECT_POINTS` bir doğru eşleşmenin kaç puan olduğunu belirtir. `GameSession.score` ise o anda oynanan turun puanıdır. `score += GameRules.CORRECT_POINTS` ifadesi, mevcut puana kuraldaki sayıyı ekler. Kurallar sabit, tur durumu değişkendir.

## private, public, static, final

- `private int score`: başka sınıflar bu alanı doğrudan değiştiremez. `attempt` üzerinden kurala uygun değişir.
- `public int score()`: dışarıya yalnızca mevcut değeri okuma imkânı verir.
- `static`: üyeyi belirli bir nesneye değil sınıfa bağlar; örneğin puan sabiti.
- `final`: alanın referansı/değeri bir kez atandıktan sonra tekrar atanamaz. Bir listeyi tek başına değiştirilemez yapmaz; bu yüzden `List.copyOf` kullanılır.

## constructor ve this

`GameSession(...)` bir tur nesnesi oluşturulurken çalışır. Kelime sayısı/kimlikleri/zorluk geçerli mi kontrol eder. `this.difficulty = difficulty` satırında sol taraf nesnenin alanı, sağ taraf metoda gelen parametredir. `secondsRemaining = difficulty.seconds` seçilen seviyenin süresini başlangıç değeri yapar.

## record ve enum

`Word` bir Java record'udur: kimlik, kategori ve çeviriler gibi verileri taşır; alanları final'dır, okuma metotlarını Java üretir. Eklediğimiz kurucu doğrulaması eksik/boş kelimeyi reddeder. `Language` ve `Difficulty` enum'ları yalnızca tanımlanmış seçenekleri kabul eder.

## Bir eşleşme nasıl kontrol ediliyor?

1. Arayüz sol ve sağ kelimenin kimliğini `attempt` metoduna verir.
2. Tur bitmişse, geri bildirim sürüyorsa veya kart zaten eşleşmişse `IGNORED` döner.
3. Kimlikler `equals` ile aynıysa doğru eşleşmedir. Metin çevirilerini birbirine eşitlemeyiz; aynı kavramın kimliğini karşılaştırırız.
4. Doğru kimlik `matched` kümesine eklenir ve +10 puan verilir. Tüm kelimeler bittiyse `COMPLETED` olur.
5. Yanlışsa −5 puan ve bir yanlış deneme eklenir. `finishFeedback` kilidi kaldırır.

## List ve Set neden var?

`List<Word>` sıralı kelimeleri tutar; `<Word>` yalnızca Word nesneleriyle çalıştığımızı söyler. `Set<String>` aynı kimliği iki kez tutmaz; eşleşen kartları tekrar saymamak için uygundur. `HashSet` bir Set uygulamasıdır. `ArrayList` bir List uygulamasıdır.

## interface, implements, Override, polimorfizm

`WordOrder` arayüzü “arrange metodunu sunmalısın” sözleşmesidir. `ShuffledWordOrder implements WordOrder`, bu sözleşmeyi uyguladığını söyler. `@Override`, arayüzdeki metodu burada gerçekleştirdiğimizi derleyiciye belirtir.

`WordOrder policy = new AlphabeticalWordOrder()` satırında değişkenin tipi arayüz, gerçek nesnenin tipi alfabetik stratejidir. `policy.arrange(...)` çağrısında nesnenin kendi gövdesi çalışır. Başka somut nesne atarsak aynı çağrı farklı davranır. Bu **çalışma zamanı polimorfizmi** ve **Strategy** deseninin merkezidir. Sadece aynı isimli iki metot yazmak polimorfizm kanıtı değildir; ortak tip üzerinden çağrıyı göster.

## Sıralama ve lambda

`Comparator.comparing(word -> word.text(language), collator)` sıralamanın ölçütünü tanımlar. Lambda “bu kelimeden seçilen dildeki metni çıkar” der. `Collator`, o dilin alfabetik karşılaştırmasını yapar. `result.sort(...)` listeyi bu karşılaştırıcıyla sıralar.

## Hata yönetimi

`throw new InvalidGameException(...)` geçersiz oyun girdisini bildirir. Bu, Java'nın `IllegalArgumentException` sınıfından türetilmiş özel unchecked exception'dır. Konsol bunu yakalayıp kullanıcıdan yeni giriş ister. `IOException` dosya/okuma/yazma hatalarını ifade eder; oyun alan hatasından farklıdır.

## Kalıcılık ve try-with-resources

`WordRepository` paket içindeki TSV dosyasını okur. `AppSettings` kullanıcı tercihlerinin Properties dosyasını yazar/yükler. `try (Reader reader = ...)` bloğu bittiğinde Reader'ı hata olsa da kapatır. Kaydın önce geçici dosyaya yazılması, yarım kalmış ana ayar dosyası riskini azaltır.

## Arayüz niye donmuyor?

Swing işlemleri EDT'de yapılır. Uzun dosya veya ses işlemi EDT'yi bekletirse butonlar cevap veremez. Bu yüzden ExecutorService arka plan işçisi kullanılır; ekrana geri dönmek gerektiğinde SwingUtilities.invokeLater çağrılır. Testte disk yazımı bekletilerek menünün hâlâ çalıştığı doğrulanır.

## Testi nasıl açıklarsın?

JUnit'te `@Test` bir test metodunu belirtir. `assertEquals(-5, game.score())`, yanlış eşleşmeden sonra beklediğimiz puan ile gerçeği karşılaştırır. `assertThrows`, geçersiz girişin beklenen hatayı verdiğini doğrular. `@TempDir` gerçek ayarlarını bozmadan test için geçici dosya alanı sağlar.
