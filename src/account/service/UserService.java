package account.service;

import account.dto.PasswordStatusDto;
import account.dto.UserCreateDto;
import account.dto.UserGetDto;
import account.exception.PasswordBreachedException;
import account.exception.PasswordSameException;
import account.exception.UserExistException;
import account.mapper.Mapper;
import account.model.User;
import account.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String[] breachedPasswords = {
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
    };

    public UserGetDto signUp(UserCreateDto userCreateDto) {
        logger.info("Registering \"" + userCreateDto + "\"");
        userCreateDto.setEmail(userCreateDto.getEmail().toLowerCase());
        breachedPassword(userCreateDto.getPassword());

        boolean isUserPresent = userRepository
                .findByUsername(userCreateDto.getEmail())
                .isPresent();
        if (isUserPresent) {
            throw new UserExistException();
        }

        userCreateDto.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        User user = userRepository.save(mapper.userCreateDtoToUser(userCreateDto));
        return mapper.userToUserGetDto(user);
    }

    @Transactional
    public PasswordStatusDto changePassword(String password, String username) {
        logger.info("Changing password \"" + username + "\"");
        breachedPassword(password);

        User user = loadUserByUsername(username);
        if ( passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordSameException();
        }

        userRepository.updatePassword(passwordEncoder.encode(password), username);
        return PasswordStatusDto.builder()
                .email(username)
                .status("The password has been updated successfully")
                .build();
    }

    public UserGetDto findUser(String username) {
        User user = loadUserByUsername(username);
        return mapper.userToUserGetDto(user);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private void breachedPassword(String password) {
        boolean isPasswordBreached = Arrays.asList(breachedPasswords).contains(password);
        if (isPasswordBreached) {
            throw new PasswordBreachedException();
        }
    }
}
