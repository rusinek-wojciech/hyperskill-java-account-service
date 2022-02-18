package account.model.user;

import account.repository.RoleRepository;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    USER,
    ACCOUNTANT,
    ADMINISTRATOR;

    RoleEntity toRoleEntity(RoleRepository repository) {
        return repository.findByRole(this).orElseThrow();
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
