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
import account.validator.Validators;
import lombok.AllArgsConstructor;
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

    public GetUserDto register(CreateUserDto dto) {
        String username = mapper.emailToUsername(dto.getEmail());
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ValidException("User exist!");
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Role role = userRepository.count() == 0L
                ? Role.ADMINISTRATOR
                : Role.USER;
        User user = User.builder()
                .name(dto.getName())
                .lastname(dto.getLastname())
                .username(username)
                .password(encodedPassword)
                .roles(Set.of(role), roleRepository)
                .build();
        eventService.log(Action.CREATE_USER, user.getEmail(), (String) null);
        return mapper.userToGetUserDto(userRepository.save(user));
    }

    @Transactional
    public User changePassword(User user, String password) {
        Validators.validatePasswordSame(password, user, passwordEncoder);
        user.setPassword(passwordEncoder.encode(password));
        eventService.log(Action.CHANGE_PASSWORD, user.getEmail(), user);
        return userRepository.save(user);
    }

    public GetUserDto getCurrentUser(User user) {
        return mapper.userToGetUserDto(user);
    }

}
