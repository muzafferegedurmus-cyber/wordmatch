# Problem ve tasarım kararları

## Problem

Yabancı dil öğrenen kullanıcı, kısa bir sürede kelimelerin karşılıklarını tekrar etmek ve cevabını hemen kontrol etmek ister. WordMatch, iki farklı dildeki kartları eşleştirerek bunu sağlar. Kullanıcı bilgisayarda grafik pencereyi veya terminali kullanabilir. Tercihler sonraki açılışa taşınır.

## Strategy neden var?

Standart oyun rastgele sütunlar ister. Öğrenme aşamasındaki kullanıcı konsolda sözcükleri alfabetik sırada görerek arama yükünü azaltmak isteyebilir. Her iki arayüzde veya modelde `if (alphabetical)` dalları çoğaltmak yerine sıralama bir sözleşmeye ayrıldı:

```java
WordOrder policy = order == 1
        ? new ShuffledWordOrder()
        : new AlphabeticalWordOrder();
controller.setWordOrder(policy);
```

`GameController.column` şu işi yapar:

```java
return wordOrder.arrange(session.words(), language, random);
```

`WordOrder` Strategy arayüzü; iki somut sınıf ConcreteStrategy; `GameController` Context rolündedir. Hangi `arrange` gövdesinin çağrılacağı gerçek nesne tipine göre çalışma zamanında belirlenir. Puan, süre ve doğru eşleşme davranışı stratejiye bağlı değildir. Varsayılan Swing davranışı bağımsız karıştırma olarak korunmuştur.

## Sorumluluklar ve ilişkiler

- `WordRepository`, UTF-8 veriden oluşturduğu en az 75 değiştirilemeyen `Word` nesnesini içerir (composition). Bir kelime aynı havuzdan birden fazla tura seçilebilir.
- `GameSession`, havuzdan seçilmiş 4–8 `Word` nesnesine başvurur (aggregation); skor, yanlış sayısı, süre ve bitiş nedenini kapsüller.
- `GameController`, bir `WordRepository` ve bir `WordOrder` kullanır (aggregation); tur oluşturur ve sütunları düzenler.
- `WordMatchApp` ve `ConsoleApp`, aynı domain ve service kodunu kullanır. Arayüz olaylarının çoğu Swing sınıfında kaldığından eksiksiz bir MVC uygulaması iddia edilmez. Ödev için açıkça seçilen desen Strategy'dir.
- `WordMatchApp`, `SoundEffects` ve controller'ın yaşam döngüsünü yönetir; kapanırken ses kaynaklarını ve zamanlayıcıları kapatır.
- `AppSettings`, getter/setter ile kontrollü erişim sunar. Dil seçimleri form doldurulurken geçici olarak aynı olabilir; tur başlatılırken `GameController` iki farklı dil şartını uygular.

## Kararlar

1. Baştan yazmak yerine mevcut Swing sınıfları paketlere taşındı. Menü, görsel tasarım, ses, dil desteği ve temel oyun korundu.
2. Java 21 `record Word`, değiştirilemeyen bir değer nesnesidir; kurucu eksik veriyi reddeder ve listeyi kopyalar.
3. İki sütun kelime metnini değil aynı kavramın kimliğini eşleştirir. Aynı yazımlı çevirilerin bir turda tekrarı engellenir.
4. Koleksiyonlar dışarıya değiştirilemez kopyalarla sunulur. Ayarlarda public mutable alan yoktur.
5. GUI'de `javax.swing.Timer`; konsolda `System.nanoTime` ve zaman aşımıyla kuyruktan okuma kullanılır. Böylece konsol girdisi Enter beklerken turun süresi yine dolar.
6. Disk yazımı GUI iş parçacığını bekletmez. Her yazım değişmez bir Properties anlık kopyasıyla sıraya alınır.
7. Maven Wrapper sabit Maven sürümü kullanır. Normal JUnit testleri grafik ortam istemez; gerçek Swing testi ayrıca çalıştırılır.

## Sınırlar

Sözcük havuzu elle hazırlanmıştır; çeviri anlamlarının kontrolü otomatize edilmez. Puan geçmişi saklanmaz. Dosya tercihleri tek kullanıcı içindir; aynı ayar dosyasını aynı anda yazan iki uygulama arasında son yazan kazanır. Konsol temayı veya sesi kullanmaz; bunlar grafik arayüze özeldir. Konsoldaki alfabetik düzen standart karıştırmalı oyundan farklı bir çalışma seçeneğidir. İşletim sistemi ses çıkışı yoksa sessiz devam edilir. İlk Maven derlemesi internet gerektirir, derlenmiş uygulama gerektirmez.

Şema: [SVG](class-diagram.svg), [PlantUML kaynağı](class-diagram.puml). Şema önemli ilişkileri gösterir; iç Swing çizim sınıfları ve bütün yardımcı metotlar okunabilirlik için listelenmez.
