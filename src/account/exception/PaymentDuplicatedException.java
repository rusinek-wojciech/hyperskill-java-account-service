package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Employee-period pair must be unique")
public class PaymentDuplicatedException extends RuntimeException  {
}
