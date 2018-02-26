package exceptions;

import java.io.IOException;

public class CreateDirectoriesException extends IOException {
    public CreateDirectoriesException(String message) {
        super(message);
    }
}
