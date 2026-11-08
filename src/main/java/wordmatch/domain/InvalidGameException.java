package wordmatch.domain;

/** Oyunun alan kurallarını ihlal eden girişler için özel hata. */
public class InvalidGameException extends IllegalArgumentException {
    public InvalidGameException(String message) { super(message); }
}
