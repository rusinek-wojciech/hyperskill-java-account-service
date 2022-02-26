package account.service;

import account.model.event.Action;
import account.model.user.User;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @see <a href="https://www.baeldung.com/spring-security-block-brute-force-authentication-attempts"
 */
@Service
public class LoginAttemptService {

    private final static int MAX_ATTEMPT = 5;

    private final LoadingCache<String, Integer> attempts;
    private final EventService eventService;
    private final UserService userService;
    private final HttpServletRequest request;

    public LoginAttemptService(EventService eventService,
                               UserService userService,
                               HttpServletRequest request) {
        this.eventService = eventService;
        this.userService = userService;
        this.request = request;
        attempts = CacheBuilder
                .newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void loginSucceeded(String username) {
        attempts.invalidate(username);
    }

    public void loginFailed(String username) {
        eventService.log(
                Action.LOGIN_FAILED,
                request.getRequestURI().substring(request.getContextPath().length()),
                username
        );
        attempts.put(username, Optional.ofNullable(attempts.getIfPresent(username))
                .map(n -> n + 1)
                .orElse(1));
        if (isBlocked(username)) {
            User user = userService.loadUserByUsername(username);
            userService.lock(username, user);
        }
    }

    public boolean isBlocked(String username) {
        return Optional.ofNullable(attempts.getIfPresent(username))
                .map(n -> n >= MAX_ATTEMPT)
                .orElse(false);
    }
}
