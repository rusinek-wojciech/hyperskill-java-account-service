package account.model.user;

import account.repository.RoleRepository;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    USER,
    ACCOUNTANT,
    ADMINISTRATOR;

    /**
     * for annotations
     */
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ACCOUNTANT = "ROLE_ACCOUNTANT";
    public static final String ROLE_ADMINISTRATOR= "ROLE_ADMINISTRATOR";

    RoleEntity toRoleEntity(RoleRepository repository) {
        return repository.findByRole(this).orElseThrow();
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
