package ua.training.mytestingapp.util;

public class ResponseStatusException extends RuntimeException {

    private final int status;

    public ResponseStatusException(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
