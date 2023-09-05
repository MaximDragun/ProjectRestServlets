package exceptions;

public class MySqlRuntimeException extends RuntimeException {

    public MySqlRuntimeException(String message) {
        super(message);
    }

    public MySqlRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
