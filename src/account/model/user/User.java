package account.model.user;

import account.model.Payment;
import account.repository.RoleRepository;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
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

    @NaturalId
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns =@JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    public void addRole(Role role, RoleRepository repository) {
        roles.add(role.toRoleEntity(repository));
    }

    public void removeRole(Role role) {
        roles.removeIf((r) -> r.getRole() == role);
    }

    public Set<Role> getRoles() {
        return roles.stream()
                .map(RoleEntity::getRole)
                .collect(Collectors.toSet());
    }

    public void setRoles(Set<Role> roles, RoleRepository repository) {
        this.roles = roles.stream().map(r -> r.toRoleEntity(repository)).collect(Collectors.toSet());
    }

    public RoleGroup getUserRoleGroup() {
        return getRoles().stream()
                .map(Role::getGroup)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(Role::getAuthority)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
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

        public UserBuilder roles(Set<Role> roles, RoleRepository repository) {
            this.roles = roles.stream().map(r -> r.toRoleEntity(repository)).collect(Collectors.toSet());
            return this;
        }
    }

}
