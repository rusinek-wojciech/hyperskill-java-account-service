package account.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AppUtils {

    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        try {
            return Enum.valueOf(enumType, name);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, enumType.getSimpleName() + " not found!");
        }
    }

}
