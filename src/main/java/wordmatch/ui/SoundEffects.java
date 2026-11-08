package wordmatch.ui;


import javax.sound.sampled.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/** İnternetsiz, düşük sesli ve yumuşak bir kart seçme tonu. Ses işlemleri EDT'yi bekletmez. */
public final class SoundEffects implements AutoCloseable {
    private final ExecutorService audio = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "wordmatch-audio"); thread.setDaemon(true); return thread;
    });
    private final AtomicBoolean pending = new AtomicBoolean();
    private Clip selection;
    private volatile boolean closed;
    private final Future<?> ready;

    public SoundEffects() {
        ready = audio.submit(() -> {
            Clip opened = null;
            try {
                byte[] samples = selectionSamples();
                opened = AudioSystem.getClip();
                opened.open(new AudioFormat(44100, 16, 1, true, false), samples, 0, samples.length);
                selection = opened;
            } catch (LineUnavailableException | IllegalArgumentException | SecurityException e) {
                if (opened != null) opened.close();
                System.err.println("Audio unavailable; continuing silently: " + e.getMessage());
            }
        });
    }
    static byte[] selectionSamples() {
        int count = (int)(44100 * 0.18);
        byte[] bytes = new byte[count * 2];
        for (int i = 0; i < count; i++) {
            double time = i / 44100.0;
            double remaining = (count - 1 - i) / 44100.0;
            // Yumuşak giriş/çıkış ve hafif alçalan saf ton: keskin üst harmonik yok.
            double attack = Math.sin(Math.PI * .5 * Math.min(1, time / .025));
            double release = Math.sin(Math.PI * .5 * Math.min(1, remaining / .060));
            double envelope = attack * attack * Math.exp(-15 * time) * release * release;
            double phase = 2 * Math.PI * (320 * time + 100 * .04 * (1 - Math.exp(-time / .04)));
            short sample = (short)(32767 * .06 * envelope * Math.sin(phase));
            bytes[2*i] = (byte)sample; bytes[2*i+1] = (byte)(sample >> 8);
        }
        return bytes;
    }
    public void playSelection() {
        if (closed || !pending.compareAndSet(false, true)) return;
        audio.execute(() -> {
            try {
                if (selection != null && !closed) {
                    // Hızlı tıklamalar sürmekte olan sesi kesip çıtırtı oluşturmasın.
                    if (selection.isRunning()) return;
                    selection.stop(); selection.setFramePosition(0); selection.start();
                }
            } finally { pending.set(false); }
        });
    }
    boolean availableForTest() throws Exception { ready.get(10, TimeUnit.SECONDS); return selection != null; }
    @Override public void close() {
        if (closed) return;
        closed = true;
        audio.execute(() -> { if (selection != null) { selection.stop(); selection.close(); } });
        audio.shutdown();
    }
}
