package account.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {

    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
