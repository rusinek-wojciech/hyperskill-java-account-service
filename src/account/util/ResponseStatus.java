package account.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * for providing custom response body
 */
public final class ResponseStatus {

    private final Map<String, Object> map = new LinkedHashMap<>();
    private HttpStatus httpStatus = HttpStatus.OK;

    public ResponseStatus add(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public ResponseStatus status(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public ResponseEntity<Object> build() {
        return ResponseEntity.status(httpStatus).body(map);
    }

    public static ResponseStatus builder() {
        return new ResponseStatus();
    }

}
