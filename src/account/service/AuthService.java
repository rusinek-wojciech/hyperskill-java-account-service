package account.service;

import account.dto.PasswordStatusDto;
import account.dto.UserCreateDto;
import account.dto.UserGetDto;
import account.exception.ValidException;
import account.mapper.Mapper;
import account.model.User;
import account.repository.UserRepository;
import account.validator.Validators;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@AllArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;

    public UserGetDto signUp(UserCreateDto userCreateDto) {
        log.info("Registering \"" + userCreateDto + "\"");
        userCreateDto.setEmail(userCreateDto.getEmail().toLowerCase());
        Validators.validatePasswordBreached(userCreateDto.getPassword());

        boolean isUserExist = userRepository
                .findByUsername(userCreateDto.getEmail())
                .isPresent();
        if (isUserExist) {
            throw new ValidException("User exist!");
        }

        userCreateDto.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        User user = userRepository.save(mapper.userCreateDtoToUser(userCreateDto));
        return mapper.userToUserGetDto(user);
    }

    @Transactional
    public PasswordStatusDto changePassword(User user, String password) {
        log.info("Changing password \"" + user.getUsername() + "\"");
        Validators.validatePasswordBreached(password);
        Validators.validatePasswordSame(password, user, passwordEncoder);
        userRepository.updatePassword(passwordEncoder.encode(password), user.getUsername());
        return PasswordStatusDto.builder()
                .email(user.getUsername())
                .status("The password has been updated successfully")
                .build();
    }
}
