package account.service;

import account.dto.user.CreateUserDto;
import account.dto.user.GetUserDto;
import account.exception.ValidException;
import account.mapper.Mapper;
import account.model.event.Action;
import account.model.user.Role;
import account.repository.RoleRepository;
import account.model.user.User;
import account.repository.UserRepository;
import account.util.ResponseStatus;
import account.validator.Validators;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final Mapper mapper;
    private final EventService eventService;

    public GetUserDto register(CreateUserDto createUserDto) {
        Role role = userRepository.count() == 0L
                ? Role.ADMINISTRATOR
                : Role.USER;
        createUserDto.setEmail(createUserDto.getEmail().toLowerCase());

        if (userRepository.findByUsername(createUserDto.getEmail()).isPresent()) {
            throw new ValidException("User exist!");
        }

        createUserDto.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        User user = mapper.createUserDtoToUser(createUserDto, Set.of(role), roleRepository);
        eventService.log(Action.CREATE_USER, user.getUsername(), (String) null);
        return mapper.userToGetUserDto(userRepository.save(user));
    }

    @Transactional
    public ResponseEntity<?> changePassword(User user, String password) {
        Validators.validatePasswordSame(password, user, passwordEncoder);
        userRepository.updatePassword(passwordEncoder.encode(password), user.getUsername());
        eventService.log(Action.CHANGE_PASSWORD, user.getUsername(), user);
        return ResponseStatus.builder()
                .add("email", user.getUsername())
                .add("status", "The password has been updated successfully")
                .build();
    }


}
