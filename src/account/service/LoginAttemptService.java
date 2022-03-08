package account.service;

import account.model.event.Action;
import account.model.user.Role;
import account.model.user.User;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.Lazy;
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

    public LoginAttemptService(EventService eventService,
                               @Lazy UserService userService,
                               HttpServletRequest request) {
        this.eventService = eventService;
        this.userService = userService;
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

    public void loginSucceeded(User user) {
        System.out.println("Login success " + user.getEmail());
        attempts.invalidate(user.getEmail());
    }

    public void loginFailed(String email) {
        System.out.println("Login failed " + email);
        eventService.log(Action.LOGIN_FAILED, email);
        attempts.put(email, Optional.ofNullable(attempts.getIfPresent(email))
                .map(n -> n + 1)
                .orElse(1));
        if (isBlocked(email)) {
            User user = userService.loadUserByUsername(email);
            if (user.getAccountNonLocked() && !user.getRoles().contains(Role.ADMINISTRATOR)) {
                eventService.log(Action.BRUTE_FORCE, email);
                userService.lock(email, user);
            }
        }
    }

    public void clean(String email) {
        attempts.invalidate(email);
    }

    public boolean isBlocked(String email) {
        return Optional.ofNullable(attempts.getIfPresent(email.toLowerCase()))
                .map(n -> n >= MAX_ATTEMPT)
                .orElse(false);
    }
}
