package account.service;

import account.dto.PasswordStatusDto;
import account.dto.PasswordUpdateDto;
import account.dto.UserCreateDto;
import account.dto.UserGetDto;
import account.exception.PasswordBreachedException;
import account.exception.PasswordSameException;
import account.exception.UserExistException;
import account.mapper.Mapper;
import account.model.User;
import account.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final Mapper mapper;
    private final AuthFacade authFacade;
    private final UserService userService;

    private static final Set<String> breachedPasswords = Set.of(
            "PasswordForJanuary",
            "PasswordForFebruary",
            "PasswordForMarch",
            "PasswordForApril",
            "PasswordForMay",
            "PasswordForJune",
            "PasswordForJuly",
            "PasswordForAugust",
            "PasswordForSeptember",
            "PasswordForOctober",
            "PasswordForNovember",
            "PasswordForDecember"
    );

    public UserGetDto signUp(UserCreateDto userCreateDto) {
        log.info("Registering \"" + userCreateDto + "\"");
        userCreateDto.setEmail(userCreateDto.getEmail().toLowerCase());

        boolean isPasswordBreached = breachedPasswords.contains(userCreateDto.getPassword());
        if (isPasswordBreached) {
            throw new PasswordBreachedException();
        }

        boolean isUserExist = userRepository
                .findByUsername(userCreateDto.getEmail())
                .isPresent();
        if (isUserExist) {
            throw new UserExistException();
        }

        userCreateDto.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        User user = userRepository.save(mapper.userCreateDtoToUser(userCreateDto));
        return mapper.userToUserGetDto(user);
    }

    @Transactional
    public PasswordStatusDto changePassword(PasswordUpdateDto passwordUpdateDto) {
        String username = authFacade.getAuth().getName();
        return changePassword(passwordUpdateDto.getNewPassword(), username);
    }

    @Transactional
    public PasswordStatusDto changePassword(String password, String username) {
        log.info("Changing password \"" + username + "\"");

        boolean isPasswordBreached = breachedPasswords.contains(password);
        if (isPasswordBreached) {
            throw new PasswordBreachedException();
        }

        User user = userService.loadUserByUsername(username);
        boolean isPasswordSame = passwordEncoder
                .matches(password, user.getPassword());
        if (isPasswordSame) {
            throw new PasswordSameException();
        }

        userRepository.updatePassword(passwordEncoder.encode(password), username);
        return PasswordStatusDto.builder()
                .email(username)
                .status("The password has been updated successfully")
                .build();
    }
}
