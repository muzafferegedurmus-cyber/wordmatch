import javax.swing.SwingUtilities;

/** Uygulamanın başlangıç noktası. Swing bileşenleri yalnızca EDT üzerinde oluşturulur. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WordMatchApp().setVisible(true));
    }
}
