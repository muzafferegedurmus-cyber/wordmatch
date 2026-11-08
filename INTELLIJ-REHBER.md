# IntelliJ IDEA: güncellenmiş WordMatch projesi

## Aç ve çalıştır

1. Mevcut eski projeyi kapat; **File → Open** ile bu klasördeki **pom.xml** dosyasını seç, **Open as Project** de. Kaynakları elle “Sources Root” olarak işaretleme; Maven düzeni bunu belirler.
2. **File → Project Structure → Project SDK**: JDK 21 veya daha yeni bir JDK seç. Bu bilgisayarda `C:\Program Files\Java\jdk-21.0.12.1` bulunuyor.
3. Maven panelinden **Reload All Maven Projects** çalıştır. İlk eşitlemede internet gerekir.
4. `src/main/java/wordmatch/Main.java` dosyasını aç. `main` yanındaki yeşil **Run** düğmesi pencereyi açar.
5. Konsol için **Run → Edit Configurations → Program arguments** alanına `--console` yaz. Aynı Main sınıfı bu kez terminal menüsünü açar.

## Terminal panelinde gösterebileceğin komutlar

```powershell
# Projeyi temizle, derle, JUnit testlerini çalıştır ve JAR oluştur
.\mvnw.cmd clean verify

# Swing arayüzü
java -jar target/wordmatch.jar

# Konsol arayüzü
chcp 65001
java -jar target/wordmatch.jar --console
```

JDK seçimi terminale yansımıyorsa bu bilgisayarda oturum için:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.12.1'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

`clean` önceki Maven çıktılarını temizler. `verify` derleme, test ve paketleme aşamalarını çalıştırır. `java -jar` oluşan paketi başlatır. `--console` bizim Main sınıfının yorumladığı uygulama argümanıdır.

## Kodları hangi sırayla çalışmalısın?

1. `Main`: başlangıç ve arayüz seçimi.
2. `GameRules`, `Language`, `Word`: kurallar, enum ve değiştirilemeyen kelime nesnesi.
3. `GameSession`: bir yanlış ve bir doğru denemede hangi alanların değiştiğini takip et.
4. `WordRepository`: dosya okuma ve tekrarsız kelime seçimi.
5. `WordOrder` ve uygulamaları: aynı metot çağrısının farklı somut nesnelerde farklı çalışması.
6. `GameController`: turu kurup seçilmiş politikayı çağırır.
7. `ConsoleApp`, ardından `WordMatchApp`: modelin farklı arayüzlerden kullanımı.
8. `AppSettings`: yükle, değiştir, kaydet; try-with-resources ve hatalar.

[Sözlü anlatım rehberi](docs/oral-guide.md) her temel kavramı bu kodla eşleştirir. [UML](docs/class-diagram.svg) sınıfların ilişkilerini gösterir. Anlamadığın kodu ezberlemek yerine, girişini, yaptığı değişikliği ve çıktısını anlat.
