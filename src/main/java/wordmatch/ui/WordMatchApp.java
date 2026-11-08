package wordmatch.ui;

import wordmatch.domain.*;
import wordmatch.persistence.*;
import wordmatch.service.*;
import wordmatch.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/** Bütün ekranlar tek pencerede gösterilir. Oyun durumu GameSession içinde tutulur. */
public class WordMatchApp extends JFrame {
    private final AppSettings settings;
    private final SoundEffects sounds = new SoundEffects();
    private final ExecutorService settingsWriter = Executors.newSingleThreadExecutor(r -> new Thread(r, "wordmatch-settings"));
    private Future<?> latestSave;
    private WordRepository repository;
    private GameController controller;
    private JPanel root;
    private Color background, surface, foreground, muted, border, accent;
    private GameSession session;
    private javax.swing.Timer countdown, feedbackTimer;
    private WordCard selectedLeft, selectedRight;
    private final List<WordCard> cards = new ArrayList<>();
    private JLabel scoreLabel, timeLabel, progressLabel, statusLabel;
    private JProgressBar progressBar;

    public WordMatchApp() { this(new AppSettings()); }
    public WordMatchApp(AppSettings settings) {
        super("WordMatch");
        this.settings = settings;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(980, 850);
        setMinimumSize(new Dimension(860, 760));
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent event) { stopTimers(); sounds.close(); settingsWriter.shutdown(); }
        });
        try { repository = new WordRepository(); controller = new GameController(repository); }
        catch (IOException e) {
            System.err.println(e.getMessage());
            JOptionPane.showMessageDialog(this, t("dataError"), t("error"), JOptionPane.ERROR_MESSAGE);
        }
        showMenu();
    }
    private String t(String key, Object... args) { return I18n.text(settings.getInterfaceLanguage(), key, args); }
    private void colors() {
        background = new Color(settings.isDark() ? 0x111B29 : 0xF3F7F4);
        surface = new Color(settings.isDark() ? 0x1D2A3A : 0xFFFFFF);
        foreground = new Color(settings.isDark() ? 0xEDF3FA : 0x20342D);
        muted = new Color(settings.isDark() ? 0xACBDCE : 0x61766B);
        border = new Color(settings.isDark() ? 0x3B4C60 : 0xD7E3DB);
        accent = new Color(switch (settings.getAccent()) {
            case "blue" -> settings.isDark() ? 0x60A5FA : 0x1765C0;
            case "purple" -> settings.isDark() ? 0xC4A0FF : 0x7946BA;
            default -> settings.isDark() ? 0x65D89A : 0x20834B;
        });
    }
    private JPanel newScreen() {
        colors();
        root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(background);
        root.setBorder(new EmptyBorder(26, 44, 26, 44));
        setContentPane(root);
        return root;
    }
    private JPanel panel(LayoutManager layout) {
        JPanel p = new JPanel(layout); p.setOpaque(false); return p;
    }
    private JLabel label(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, size));
        label.setForeground(color);
        return label;
    }
    private JLabel centered(String text, int size, Color color) {
        JLabel label = label(text, size, color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    private RoundedButton button(String text, String name, Runnable action, boolean primary) {
        RoundedButton button = new RoundedButton(text, primary ? accent : surface, primary ? (settings.isDark() ? background : Color.WHITE) : foreground);
        button.setName(name);
        button.addActionListener(event -> action.run());
        return button;
    }
    private void finishScreen() { revalidate(); repaint(); }
    private JPanel vertical() { JPanel p = panel(null); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); return p; }
    private void gap(JPanel p, int height) { p.add(Box.createVerticalStrut(height)); }
    private void addWide(JPanel p, JComponent component, int height) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        component.setMaximumSize(new Dimension(560, height));
        component.setPreferredSize(new Dimension(560, height));
        p.add(component);
    }
    private void header(String title, Runnable back) {
        JPanel top = panel(new BorderLayout(16, 0));
        JLabel brand = label("WordMatch", 23, accent); brand.setFont(brand.getFont().deriveFont(Font.BOLD));
        brand.setName("brand");
        top.add(brand, BorderLayout.WEST);
        if (back != null) top.add(button(t("menu"), "menu", back, false), BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);
    }
    private void showMenu() {
        stopTimers();
        if (session != null) session.abandon();
        newScreen();
        JPanel center = vertical();
        center.add(Box.createVerticalGlue());
        center.add(centered("W", 68, accent));
        gap(center, 10);
        center.add(centered(t("tagline"), 13, muted));
        gap(center, 14);
        JLabel title = centered("WordMatch", 54, foreground);
        title.setFont(title.getFont().deriveFont(Font.BOLD)); center.add(title);
        gap(center, 10); center.add(centered(t("welcome"), 19, muted));
        gap(center, 36);
        RoundedButton play = button(t("play") + "  →", "play", this::showSetup, true);
        play.setEnabled(repository != null); addWide(center, play, 58);
        gap(center, 12); addWide(center, button(t("how"), "how", this::showInstructions, false), 52);
        gap(center, 12); addWide(center, button(t("settings"), "settings", this::showSettings, false), 52);
        gap(center, 12); addWide(center, button(t("exit"), "exit", this::dispose, false), 52);
        center.add(Box.createVerticalGlue());
        root.add(center, BorderLayout.CENTER);
        root.add(centered(t("offline"), 14, muted), BorderLayout.SOUTH);
        finishScreen();
    }
    private void showInstructions() {
        newScreen(); header(t("how"), this::showMenu);
        JPanel content = vertical(); gap(content, 65);
        content.add(centered(t("how"), 30, foreground)); gap(content, 28);
        JTextArea instructions = new JTextArea(t("rules", GameRules.CORRECT_POINTS, GameRules.WRONG_POINTS));
        instructions.setEditable(false); instructions.setLineWrap(true); instructions.setWrapStyleWord(true);
        instructions.setFont(new Font("SansSerif", Font.PLAIN, 19)); instructions.setForeground(foreground);
        instructions.setBackground(surface); instructions.setBorder(new EmptyBorder(24, 24, 24, 24));
        addWide(content, instructions, 370);
        root.add(content); finishScreen();
    }
    private <T> JComboBox<T> combo(T[] choices, T selected, String name) {
        JComboBox<T> box = new JComboBox<>(choices); box.setSelectedItem(selected); box.setName(name);
        box.setFont(new Font("SansSerif", Font.PLAIN, 18));
        box.setBackground(surface); box.setForeground(foreground);
        box.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean chosen, boolean focus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, chosen, focus);
                l.setBorder(new EmptyBorder(8, 12, 8, 12));
                if (value instanceof GameRules.Difficulty d) l.setText(t(d.labelKey) + " — " + t("levelInfo", d.pairs, d.seconds));
                else if (value instanceof String key) l.setText(t(key));
                l.setBackground(chosen ? accent : surface);
                l.setForeground(chosen ? (settings.isDark() ? background : Color.WHITE) : foreground);
                return l;
            }
        });
        return box;
    }
    private void field(JPanel p, String key, JComponent component) {
        JLabel caption = label(t(key), 16, muted);
        caption.putClientProperty("textKey", key);
        caption.setLabelFor(component);
        addWide(p, caption, 28); gap(p, 6); addWide(p, component, 48); gap(p, 20);
    }
    private void showSetup() {
        newScreen(); header("", this::showMenu);
        JPanel form = vertical(); gap(form, 38);
        form.add(centered(t("setup"), 26, foreground)); gap(form, 30);
        JComboBox<Language> known = combo(Language.values(), settings.getKnownLanguage(), "known");
        JComboBox<Language> practice = combo(Language.values(), settings.getPracticeLanguage(), "practice");
        JComboBox<GameRules.Difficulty> difficulty = combo(GameRules.Difficulty.values(), settings.getDifficulty(), "difficulty");
        field(form, "known", known); field(form, "practice", practice); field(form, "difficulty", difficulty);
        JLabel warning = centered(" ", 16, new Color(0xCB4343)); addWide(form, warning, 28); gap(form, 12);
        RoundedButton start = button(t("start") + "  →", "start", () -> {
            settings.setKnownLanguage((Language) known.getSelectedItem());
            settings.setPracticeLanguage((Language) practice.getSelectedItem());
            settings.setDifficulty((GameRules.Difficulty) difficulty.getSelectedItem());
            saveSettings(); startRound();
        }, true);
        Runnable validate = () -> { boolean valid = known.getSelectedItem() != practice.getSelectedItem(); start.setEnabled(valid); warning.setText(valid ? " " : t("different")); };
        known.addActionListener(e -> validate.run()); practice.addActionListener(e -> validate.run()); validate.run();
        addWide(form, start, 58); root.add(form); finishScreen();
    }
    private void saveSettings() {
        Properties snapshot = settings.snapshot();
        latestSave = settingsWriter.submit(() -> {
            try { settings.save(snapshot); }
            catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    if (isDisplayable()) JOptionPane.showMessageDialog(this, t("saveError"), t("error"), JOptionPane.WARNING_MESSAGE);
                });
            }
        });
    }
    private void showSettings() {
        newScreen(); header("", this::showMenu);
        JPanel form = vertical(); gap(form, 20);
        JLabel title = centered(t("settings"), 32, foreground); title.putClientProperty("textKey", "settings");
        form.add(title); gap(form, 20);
        JComboBox<Language> language = combo(Language.values(), settings.getInterfaceLanguage(), "interface");
        JComboBox<String> theme = combo(new String[]{"light", "dark"}, settings.isDark() ? "dark" : "light", "theme");
        JComboBox<String> accentBox = combo(new String[]{"green", "blue", "purple"}, settings.getAccent(), "accent");
        JComboBox<String> sound = combo(new String[]{"soundOn", "soundOff"}, settings.isSoundEnabled() ? "soundOn" : "soundOff", "sound");
        field(form, "interface", language); field(form, "theme", theme); field(form, "accent", accentBox);
        field(form, "sound", sound);
        // Açık JComboBox'ın olay işleyicisinde bileşen ağacını sökmek yerine yerinde güncelle.
        language.addActionListener(e -> { settings.setInterfaceLanguage((Language) language.getSelectedItem()); applySettings(); });
        theme.addActionListener(e -> { settings.setDark("dark".equals(theme.getSelectedItem())); applySettings(); });
        accentBox.addActionListener(e -> { settings.setAccent((String) accentBox.getSelectedItem()); applySettings(); });
        sound.addActionListener(e -> { settings.setSoundEnabled("soundOn".equals(sound.getSelectedItem())); applySettings(); if (settings.isSoundEnabled()) sounds.playSelection(); });
        JLabel hint = centered(t("saved"), 14, muted); hint.putClientProperty("textKey", "saved");
        gap(form, 6); form.add(hint);
        root.add(form); finishScreen();
    }
    private void applySettings() {
        colors(); root.setBackground(background);
        refreshSettingsComponents(root);
        finishScreen();
        saveSettings();
    }
    private void refreshSettingsComponents(Container parent) {
        for (Component component : parent.getComponents()) {
            if (component instanceof JLabel label) {
                Object key = label.getClientProperty("textKey");
                if (key != null) label.setText(t(key.toString()));
                label.setForeground("brand".equals(label.getName()) ? accent : "settings".equals(key) ? foreground : muted);
            } else if (component instanceof RoundedButton button) {
                button.fill = surface; button.setForeground(foreground);
                if ("menu".equals(button.getName())) button.setText(t("menu"));
            } else if (component instanceof JComboBox<?> box) {
                box.setBackground(surface); box.setForeground(foreground); box.repaint();
                continue; // ComboBox'ın kendi alt bileşenlerini ve popup yaşam döngüsünü koru.
            }
            if (component instanceof Container nested) refreshSettingsComponents(nested);
        }
    }
    private void startRound() {
        stopTimers();
        try { session = controller.startRound(settings.getDifficulty(), settings.getKnownLanguage(), settings.getPracticeLanguage()); }
        catch (IllegalStateException | IllegalArgumentException e) { JOptionPane.showMessageDialog(this, t("dataError"), t("error"), JOptionPane.ERROR_MESSAGE); showSetup(); return; }
        selectedLeft = null; selectedRight = null; cards.clear();
        newScreen(); header("", this::showMenu);
        JPanel game = panel(new BorderLayout(0, 16));
        JPanel dashboard = vertical();
        JPanel stats = panel(new GridLayout(1, 3, 20, 0));
        scoreLabel = centered("", 27, foreground); timeLabel = centered("", 27, foreground); progressLabel = centered("", 27, foreground);
        stats.add(statBox(t("score"), scoreLabel)); stats.add(statBox(t("time"), timeLabel)); stats.add(statBox(t("progress"), progressLabel));
        addWideUnlimited(dashboard, stats, 85); gap(dashboard, 14);
        progressBar = new JProgressBar(0, session.words().size());
        progressBar.setForeground(accent); progressBar.setBackground(border); progressBar.setBorderPainted(false);
        addWideUnlimited(dashboard, progressBar, 8); gap(dashboard, 16);
        JPanel languages = panel(new GridLayout(1, 2, 28, 0));
        languages.add(centered(settings.getKnownLanguage().nativeName, 18, muted)); languages.add(centered(settings.getPracticeLanguage().nativeName, 18, muted));
        addWideUnlimited(dashboard, languages, 28); game.add(dashboard, BorderLayout.NORTH);
        JPanel columns = panel(new GridLayout(1, 2, 28, 0));
        columns.add(wordColumn(true)); columns.add(wordColumn(false)); game.add(columns, BorderLayout.CENTER);
        statusLabel = centered(t("prompt"), 17, muted); statusLabel.setPreferredSize(new Dimension(100, 35)); game.add(statusLabel, BorderLayout.SOUTH);
        root.add(game); updateStats(); finishScreen();
        countdown = new javax.swing.Timer(1000, e -> {
            session.tick(); updateStats();
            if (session.finished()) showResults();
        });
        countdown.start();
    }
    private void addWideUnlimited(JPanel p, JComponent component, int height) {
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height)); component.setPreferredSize(new Dimension(600, height)); p.add(component);
    }
    private JPanel statBox(String title, JLabel value) {
        JPanel box = vertical(); box.setOpaque(true); box.setBackground(surface); box.setBorder(new EmptyBorder(10, 8, 10, 8));
        box.add(centered(title, 12, muted)); gap(box, 4); box.add(value); return box;
    }
    private JPanel wordColumn(boolean left) {
        JPanel column = panel(new GridLayout(session.words().size(), 1, 0, 9));
        List<Word> shuffled = controller.column(session, left ? settings.getKnownLanguage() : settings.getPracticeLanguage());
        for (Word word : shuffled) { WordCard card = new WordCard(word, left); cards.add(card); column.add(card); }
        return column;
    }
    private void select(WordCard card) {
        if (session.finished() || session.feedbackPending() || session.isMatched(card.word.id())) return;
        if (settings.isSoundEnabled()) sounds.playSelection();
        if (card.left) { if (selectedLeft != null) selectedLeft.reset(); selectedLeft = card; }
        else { if (selectedRight != null) selectedRight.reset(); selectedRight = card; }
        card.state = 1; card.repaint();
        if (selectedLeft == null || selectedRight == null) return;
        GameSession.Attempt result = session.attempt(selectedLeft.word.id(), selectedRight.word.id());
        if (result == GameSession.Attempt.IGNORED) return;
        WordCard a = selectedLeft, b = selectedRight;
        boolean correct = result == GameSession.Attempt.CORRECT;
        a.state = b.state = correct ? 2 : 3;
        if (correct) { a.setEnabled(false); b.setEnabled(false); }
        a.repaint(); b.repaint(); updateStats();
        statusLabel.setText(t(correct ? "correct" : "wrong", correct ? GameRules.CORRECT_POINTS : GameRules.WRONG_POINTS));
        statusLabel.setForeground(new Color(correct ? (settings.isDark() ? 0x65D89A : 0x20834B) : (settings.isDark() ? 0xFF9191 : 0xBE3030)));
        if (session.finished()) countdown.stop();
        feedbackTimer = new javax.swing.Timer(GameRules.FEEDBACK_MILLISECONDS, e -> {
            if (!correct) { a.reset(); b.reset(); }
            selectedLeft = null; selectedRight = null; session.finishFeedback();
            if (session.finished()) showResults();
            else { statusLabel.setText(t("prompt")); statusLabel.setForeground(muted); }
        });
        feedbackTimer.setRepeats(false); feedbackTimer.start();
    }
    private void updateStats() {
        scoreLabel.setText(Integer.toString(session.score()));
        timeLabel.setText(String.format(Locale.ROOT, "%02d:%02d", session.secondsRemaining() / 60, session.secondsRemaining() % 60));
        timeLabel.setForeground(session.secondsRemaining() <= 10 ? new Color(settings.isDark() ? 0xFF9191 : 0xBE3030) : foreground);
        progressLabel.setText(session.correct() + " / " + session.words().size()); progressBar.setValue(session.correct());
    }
    private void stopTimers() {
        if (countdown != null) countdown.stop();
        if (feedbackTimer != null) feedbackTimer.stop();
    }
    private void showResults() {
        stopTimers(); newScreen(); header("", this::showMenu);
        JPanel content = vertical(); gap(content, 35);
        content.add(centered(t("result"), 18, muted)); gap(content, 16);
        content.add(centered(t(session.endReason() == GameSession.EndReason.COMPLETED ? "completed" : "timeUp"), 30, foreground));
        gap(content, 24); content.add(centered(Integer.toString(session.score()), 72, accent));
        content.add(centered(t("totalScore"), 17, muted)); gap(content, 26);
        JPanel stats = panel(new GridLayout(1, 2, 18, 0));
        stats.add(statBox(t("correctCount"), centered(session.correct() + " / " + session.words().size(), 28, foreground)));
        stats.add(statBox(t("wrongCount"), centered(Integer.toString(session.mistakes()), 28, foreground)));
        addWide(content, stats, 100); gap(content, 30);
        addWide(content, button(t("again"), "again", this::startRound, true), 58); gap(content, 12);
        addWide(content, button(t("menu"), "resultMenu", this::showMenu, false), 52);
        root.add(content); finishScreen();
    }
    private class RoundedButton extends JButton {
        Color fill, ink;
        RoundedButton(String text, Color fill, Color ink) {
            super(text); this.fill = fill; this.ink = ink;
            setFont(new Font("SansSerif", Font.BOLD, 18)); setForeground(ink);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
            setBorder(new EmptyBorder(12, 22, 12, 22)); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color = fill;
            if (getModel().isPressed()) color = color.darker();
            else if (getModel().isRollover() && isEnabled()) color = blend(color, accent, 0.12f);
            if (!isEnabled()) color = blend(color, background, .5f);
            g.setColor(color); g.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 22, 22);
            g.setColor(hasFocus() ? accent : border); g.setStroke(new BasicStroke(hasFocus() ? 3 : 1));
            g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 22, 22); g.dispose();
            super.paintComponent(graphics);
        }
    }
    private static Color blend(Color a, Color b, float ratio) {
        return new Color((int)(a.getRed()*(1-ratio)+b.getRed()*ratio), (int)(a.getGreen()*(1-ratio)+b.getGreen()*ratio), (int)(a.getBlue()*(1-ratio)+b.getBlue()*ratio));
    }
    private class WordCard extends RoundedButton {
        final Word word;
        final boolean left;
        int state; // 0: normal, 1: seçili, 2: doğru, 3: yanlış
        WordCard(Word word, boolean left) {
            super(word.text(left ? settings.getKnownLanguage() : settings.getPracticeLanguage()), surface, foreground);
            this.word = word; this.left = left;
            setName((left ? "left-" : "right-") + word.id());
            setFont(new Font("SansSerif", Font.BOLD, 18)); addActionListener(e -> select(this));
        }
        void reset() { state = 0; repaint(); }
        @Override protected void paintComponent(Graphics graphics) {
            fill = switch (state) { case 1 -> blend(surface, accent, .22f); case 2 -> new Color(settings.isDark() ? 0x204F38 : 0xDBF5E5); case 3 -> new Color(settings.isDark() ? 0x653239 : 0xFFE1E1); default -> surface; };
            // Tamamlanan kartın yazısı disabled Swing renginden bağımsız okunaklı kalır.
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill); g.fillRoundRect(2, 2, getWidth()-5, getHeight()-5, 20, 20);
            g.setColor(switch(state) { case 1 -> accent; case 2 -> new Color(0x39A868); case 3 -> new Color(0xDE6565); default -> hasFocus() ? accent : border; });
            g.setStroke(new BasicStroke(state == 0 && !hasFocus() ? 1.5f : 3f)); g.drawRoundRect(2, 2, getWidth()-5, getHeight()-5, 20, 20);
            g.setColor(foreground);
            String text = (state == 2 ? "✓  " : state == 3 ? "×  " : "") + getText();
            Font font = getFont();
            while (g.getFontMetrics(font).stringWidth(text) > getWidth()-28 && font.getSize() > 12) font = font.deriveFont((float)font.getSize()-1);
            g.setFont(font); FontMetrics metrics = g.getFontMetrics();
            g.drawString(text, (getWidth()-metrics.stringWidth(text))/2, (getHeight()-metrics.getHeight())/2 + metrics.getAscent());
            g.dispose();
        }
    }
}
