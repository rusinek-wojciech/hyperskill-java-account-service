package account.service;

import account.dto.user.CreateUserDto;
import account.dto.user.GetUserDto;
import account.mapper.Mapper;
import account.model.user.Role;
import account.repository.RoleRepository;
import account.model.user.User;
import account.repository.UserRepository;
import account.util.ResponseStatus;
import account.validator.Validators;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Set;

@AllArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final Mapper mapper;

    public GetUserDto register(CreateUserDto createUserDto) {
        log.info("Registering \"" + createUserDto + "\"");
        createUserDto.setEmail(createUserDto.getEmail().toLowerCase());
        Validators.validatePasswordBreached(createUserDto.getPassword());
        Validators.validateUserExist(createUserDto.getEmail(), userRepository);
        createUserDto.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        User user = mapper.createUserDtoToUser(createUserDto, Set.of(Role.USER), roleRepository);
        return mapper.userToGetUserDto(userRepository.save(user));
    }

    @Transactional
    public ResponseEntity<?> changePassword(User user, String password) {
        log.info("Changing password \"" + user.getUsername() + "\"");
        Validators.validatePasswordBreached(password);
        Validators.validatePasswordSame(password, user, passwordEncoder);
        userRepository.updatePassword(passwordEncoder.encode(password), user.getUsername());
        return ResponseStatus.builder()
                .add("email", user.getUsername())
                .add("status", "The password has been updated successfully")
                .build();
    }
}
