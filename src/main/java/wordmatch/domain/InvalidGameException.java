package wordmatch.domain;

/** Custom exception for input that violates game-domain rules. */
public class InvalidGameException extends IllegalArgumentException {
    public InvalidGameException(String message) { super(message); }
}
