package rabbit.open.athena.plugin.common.exception;


public class AthenaException extends RuntimeException {

    public AthenaException(Throwable cause) {
        super(cause);
    }

    public AthenaException(String msg) {
        super(msg);
    }
}
