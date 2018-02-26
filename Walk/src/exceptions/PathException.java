package exceptions;

import java.nio.file.InvalidPathException;

public class PathException extends InvalidPathException {
    public PathException(String input, String reason) {
        super(input, reason);
    }
}
