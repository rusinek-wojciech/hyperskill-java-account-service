package account.model.user;

import account.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class RoleLoader {

    @Bean
    @Autowired
    CommandLineRunner initRoles(RoleRepository repository) {
        Set<RoleEntity> roles = repository.count() == 0L
                ? Arrays.stream(Role.values())
                .map(role -> RoleEntity.builder().role(role).build())
                .collect(Collectors.toSet())
                : Set.of();
        return args -> repository.saveAll(roles);
    }
}
