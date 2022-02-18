package account.model;

import account.repository.RoleRepository;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {

    USER,
    ACCOUNTANT,
    ADMINISTRATOR;

    static Set<RoleEntity> rolesToRoleEntities(Set<Role> roles,
                                               RoleRepository repository) {
        return roles.stream().map(r -> roleToRoleEntity(r, repository)).collect(Collectors.toSet());
    }

    static RoleEntity roleToRoleEntity(Role role,
                                       RoleRepository roleRepository) {
        return roleRepository.findByRole(role).orElseThrow();
    }
}
