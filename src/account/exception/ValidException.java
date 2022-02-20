package account.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class ValidException extends ResponseStatusException {
    public ValidException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
        log.warn(this.toString());
    }
}
