# WordMatch

iPad ve tarayıcı için paylaşılabilir oyun: [WordMatch Web](https://wordmatch-ipad-muzaf.subtlepause4.chatgpt.site). Bağlantıya sahip herkes açabilir; ilk açılış internet gerektirir. Java/Swing projesi masaüstü sürümüdür. IntelliJ IDEA ve ders anlatımı için [INTELLIJ-REHBER.md](INTELLIJ-REHBER.md) dosyasına bak.

Java 21 ve Swing ile çevrimdışı kelime eşleştirme oyunu. Harici Java kütüphanesi, hesap veya ağ bağlantısı gerektirmez. Java 21 veya daha yeni bir **JDK** gerekir (`java -version`, `javac -version`).

## Başlatma

Windows’ta `run.bat` dosyasına çift tıkla veya proje klasöründe PowerShell terminalini aç:

```powershell
.\run.ps1
```

PowerShell betik çalıştırmayı engellerse `run.bat` kullanabilirsin. Elle derlemek için:

```powershell
New-Item -ItemType Directory -Force build/classes
javac --release 21 -encoding UTF-8 -d build/classes *.java
Copy-Item data build/classes -Recurse -Force
java -cp build/classes Main
```

macOS/Linux: `mkdir -p build/classes && javac --release 21 -encoding UTF-8 -d build/classes *.java && cp -R data build/classes/ && java -cp build/classes Main`

VS Code’da bu klasörü aç, **Extension Pack for Java** eklentisini ve JDK 21+ kur. `Main.java` içindeki `main` üzerinde **Run** seç veya Run and Debug panelindeki **WordMatch** yapılandırmasını çalıştır. VS Code gerekmez; terminal komutları bağımsız çalışır.

## Oyun

Oyna → bildiğin dil, pratik dili ve zorluk → Turu Başlat. Türkçe, İngilizce, İtalyanca, Fransızca ve Almanca arasında 20 yönlü dil çifti desteklenir. İki dil aynı olamaz. Sütunlar ayrı ayrı karıştırılır.

Her sütundan bir kart seç. Aynı sütunda seçim değiştirilebilir. Doğru çift **+10**, yanlış deneme **−5** puan; puan negatif olabilir. Yeşil kartlar tamamlanır, kırmızı kartlar 650 ms sonra yeniden kullanılabilir. Geri bildirim sırasında yeni denemeler kilitlenir. Bütün çiftler eşleşince veya süre dolunca sonuç görünür. Menüye dönmek turu bırakır ve zamanlayıcıları durdurur. Tekrar Oyna aynı dil/zorlukla yeni rastgele tur açar.

| Seviye | Çift | Süre | Havuz |
|---|---:|---:|---:|
| Kolay | 4 | 60 sn | 25 |
| Orta | 6 | 45 sn | 25 |
| Zor | 8 | 35 sn | 25 |

Ayarlar: 5 arayüz dili, açık/koyu tema, yeşil/mavi/mor vurgu. Seçimler anında uygulanır ve kullanıcı klasöründeki `.wordmatch/settings.properties` dosyasına kaydedilir. Dil çifti ve zorluk da saklanır. Fabrika tercihleri Türkçe → İngilizce, kolay, açık tema ve yeşildir.

Kart seçildiğinde kısa ve düşük sesli bir ton çalar. Ayarlar → Oyun sesleri ile kapatılabilir; tercih saklanır. Ses yerel olarak üretilir, dosya indirilmez. Ses aygıtı kullanılamıyorsa oyun sessiz devam eder. `SoundEffects.java` içindeki ton frekansı, süre ve genlik değiştirilerek ses özelleştirilebilir. Ses işlemleri ve ayarların diske yazılması arka plandadır; ayarlar ekranı yerinde güncellenir.

## Nereden değiştirilir?

- `GameRules.java`: puanlar, geri bildirim süresi, çift sayıları ve tur süreleri.
- `data/words.tsv`: UTF-8 kelimeler. Her satır **sekme** ile ayrılmış 8 alan içerir: `id`, `difficulty`, `category`, `TR`, `EN`, `IT`, `FR`, `DE`. Zorluk `EASY`, `MEDIUM` veya `HARD`; kimlik benzersiz olmalı. Her seviyede en az 25 satır bulundur. Yeni satırı ekleyip yeniden derle/çalıştır. Kategori öğrenme içeriğini düzenlemek için saklanır, oyunda filtre değildir. Aynı dilde eş anlamlı/çok anlamlı girdiler eklerken karşılıkları kontrol et; aynı sütundaki aynı yazımlı sözcükler bir turda birlikte seçilmez.
- `WordRepository.java`: dosya okuma, doğrulama ve tekrar içermeyen rastgele seçim.
- `GameSession.java`: arayüzden bağımsız puan, deneme kilidi, eşleşme ve tur bitiş mantığı.
- `WordMatchApp.java`: Swing ekranları, kart çizimi, tema ve `javax.swing.Timer`.
- `I18n.java`: bütün menü ve oyun çevirileri; sıralama TR, EN, IT, FR, DE.
- `AppSettings.java`: tercih yükleme/kaydetme. `Main.java`: Swing EDT başlangıcı.

## Kontroller

```powershell
.\run.ps1 -Test
```

Test; 20 dil yönünü her zorlukta çok sayıda rastgele turla, doğru/yanlış ve hızlı tekrar tıklamaları, negatif puan, tur bitişleri, yeni tur ve diske ayar yazıp yeniden yüklemeyi kontrol eder. Grafik ortam varsa gerçek Swing penceresinde seçim değişimi, aynı dil engeli, sonuç/tekrar oyna, menüde zamanlayıcının durması ve pencere yeniden açıldığında tercihlerin korunması da denenir. Süre sonu testinde Timer dinleyicileri beklemeden tetiklenir. Testler kişisel ayarları değiştirmez; geçici ayar dosyası kullanır. Ekran görüntüleri `build/screenshots` klasörüne yazılır.
