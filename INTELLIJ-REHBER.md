# WordMatch — IntelliJ IDEA ve ders anlatımı

## Projeyi aç

1. IntelliJ IDEA → **Open** → bu `wordgame` klasörünü seç.
2. **File → Project Structure → Project → SDK** bölümünde JDK 21 veya daha yeni bir JDK seç. Bu bilgisayarda JDK 21, `C:\Program Files\Java\jdk-21.0.12.1` klasöründe mevcut.
3. En kolay başlangıç: IntelliJ'nin **Terminal** panelinde aşağıdaki komutları çalıştır. IDE proje yapılandırmasına ihtiyaç duymadan derlenir.
4. Editördeki yeşil Run düğmesi için proje kökünü **Sources Root** olarak işaretle; `build` ve `web-ipad` klasörlerini **Excluded** yap. `data` klasörünü **Resources Root** olarak işaretle ve Project Structure → Modules → Sources bölümünde **Relative output path** değerini `data` yap. Böylece `/data/words.tsv` sınıf yolunda bulunur. Ardından `Main.java` içindeki `main` yanındaki Run düğmesine bas.

## Terminal komutları (PowerShell)

```powershell
# Bu bilgisayardaki Java 21 araçlarını seç
$jdk = 'C:\Program Files\Java\jdk-21.0.12.1'

# Derlenmiş .class dosyalarının klasörünü oluştur
New-Item -ItemType Directory -Force build/classes

# Java kaynaklarını UTF-8 okuyarak Java 21 için derle
& "$jdk\bin\javac.exe" --release 21 -encoding UTF-8 -d build/classes *.java

# Kelime verisini sınıf yoluna kopyala
Copy-Item data build/classes -Recurse -Force

# Ana sınıfı başlat; Swing ayrı pencere açar
& "$jdk\bin\java.exe" -cp build/classes Main

# Testleri çalıştır
& "$jdk\bin\java.exe" -cp build/classes WordMatchTest
```

JDK başka bir bilgisayarda farklı klasördeyse ilk satırdaki yolu değiştir. JDK 21+ zaten PATH üzerindeyse `javac` ve `java` komutlarını doğrudan kullanabilirsin. Hazır kısa yol: `.\run.ps1` veya `run.bat`.

## Hocaya anlatabileceğin akış

| Dosya / kavram | Görevi |
|---|---|
| `Main.java`, `SwingUtilities.invokeLater` | Swing arayüzünü EDT adlı arayüz iş parçacığında başlatır. |
| `WordMatchApp.java` | Menü, ayarlar, dil/zorluk seçimi, kartlar ve sonuç ekranını çizer. |
| `GameSession.java` | Puanı, eşleşmeleri, yanlış denemeleri ve turun bitiş nedenini tutar. |
| `GameRules.java` | +10/−5 puan, çift sayıları, süreler ve geri bildirim süresi burada değişir. |
| `WordRepository.java` | TSV dosyasını UTF-8 okur; zorluk ve dillere göre rastgele, tekrarsız kelimeler seçer. |
| `data/words.tsv` | Her satırda kimlik, zorluk, kategori ve beş dilde karşılık vardır. |
| `I18n.java` | Menü ve mesajların beş dilde çevirilerini tutar. |
| `AppSettings.java` | Tercihleri dosyaya kaydeder ve açılışta yükler. |
| `SoundEffects.java` | Kısa seçim sesini üretir; ses aygıtını arka planda kullanır. |
| `javax.swing.Timer` | Her saniye geri sayımı günceller; tur bitince durdurulur. |
| `feedbackPending` | Geri bildirim sırasında aynı denemenin iki kere puanlanmasını önler. |
| `ExecutorService` | Disk ve ses işlemlerinin arayüzü bekletmesini önler. |

## iPad sürümü ile fark

Java/Swing sürümü bilgisayarda çalışır ve derste incelenecek asıl projedir. `web-ipad/dist` içindeki HTML, CSS ve JavaScript dosyaları aynı oyunun tarayıcı sürümüdür; Java kodu Safari içinde çalıştırılmaz. Web sürümü aynı 75 kelimeyi ve çevirileri kullanır. Verileri güncellemek için `python web-ipad/export-data.py` çalıştırılır. iPad sürümünün ilk açılışı internet gerektirir; bu sürümde çevrimdışı önbellekleme kurulmamıştır.
