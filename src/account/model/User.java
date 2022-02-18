package account.model;

import account.repository.RoleRepository;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns =@JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    public void addRole(Role role, RoleRepository roleRepository) {
        roles.add(Role.roleToRoleEntity(role, roleRepository));
    }

    public void removeRole(Role role) {
        roles.removeIf((r) -> r.getRole() == role);
    }

    public Set<Role> getRoles() {
        return roles.stream()
                .map(RoleEntity::getRole)
                .collect(Collectors.toSet());
    }

    public void setRoles(Set<Role> roles, RoleRepository roleRepository) {
        this.roles = Role.rolesToRoleEntities(roles, roleRepository);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    /**
     * Override Lombok builder
     */
    public static class UserBuilder {

        private Set<RoleEntity> roles;

        public UserBuilder roles(Set<Role> roles, RoleRepository roleRepository) {
            this.roles = Role.rolesToRoleEntities(roles, roleRepository);
            return this;
        }
    }


}
