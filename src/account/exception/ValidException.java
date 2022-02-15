package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ValidException extends ResponseStatusException {
    public ValidException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
