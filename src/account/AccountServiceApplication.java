package account;

import account.model.Role;
import account.model.RoleEntity;
import account.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

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