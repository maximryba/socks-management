package test.task.socks_service.exception;

public class NoEnoughSocksException extends Exception {

    public NoEnoughSocksException(String errorMessage) {
        super(errorMessage);
    }

}
