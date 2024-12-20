package test.task.socks_service.exception;

public class NoSocksFoundException extends Exception {
    public NoSocksFoundException(String errorMessage) {
        super(errorMessage);
    }
}
