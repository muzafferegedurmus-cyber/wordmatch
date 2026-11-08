package wordmatch;

import wordmatch.persistence.*;
import wordmatch.ui.*;

import javax.swing.SwingUtilities;

/** Uygulamanın başlangıç noktası. Swing bileşenleri yalnızca EDT üzerinde oluşturulur. */
public class Main {
    public static void main(String[] args) {
        if (java.util.Arrays.asList(args).contains("--console")) {
            try {
                new ConsoleApp(new java.io.InputStreamReader(System.in, java.nio.charset.StandardCharsets.UTF_8),
                        new java.io.PrintWriter(System.out, true, java.nio.charset.StandardCharsets.UTF_8),
                        new AppSettings(), new WordRepository()).run();
            } catch (java.io.IOException e) {
                System.err.println("Kelime verisi yüklenemedi / Unable to load word data: " + e.getMessage());
            }
            return;
        }
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            System.err.println("Grafik ortam yok. --console seçeneğiyle başlatın.");
            return;
        }
        SwingUtilities.invokeLater(() -> new WordMatchApp().setVisible(true));
    }
}
