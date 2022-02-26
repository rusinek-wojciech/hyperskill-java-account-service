package account.service;

import account.dto.user.GetUserDto;
import account.dto.user.UpdateLockUserDto;
import account.dto.user.UpdateRoleUserDto;
import account.exception.ValidException;
import account.mapper.Mapper;
import account.model.event.Action;
import account.model.user.Role;
import account.model.user.User;
import account.repository.RoleRepository;
import account.repository.UserRepository;
import account.util.AppUtils;
import account.util.ResponseStatus;
import account.validator.Validators;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Mapper mapper;
    private final EventService eventService;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
    }

    public List<GetUserDto> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(mapper::userToGetUserDto)
                .collect(Collectors.toList());
    }

    public GetUserDto changeUserRole(UpdateRoleUserDto dto, User user) {
        Role role = AppUtils.valueOf(Role.class, dto.getRole());
        UpdateRoleUserDto.Operation operation =
                AppUtils.valueOf(UpdateRoleUserDto.Operation.class, dto.getOperation());
        switch (operation) {
            case GRANT:
                return grantRole(role, dto.getUser(), user);
            case REMOVE:
                return removeRole(role, dto.getUser(), user);
            default:
                throw new IllegalStateException();
        }
    }

    private GetUserDto grantRole(Role role, String username, User subjectUser) {
        User user = loadUserByUsername(username);
        if (user.getUserRoleGroup() != role.getGroup()) {
            throw new ValidException("The user cannot combine administrative and business roles!");
        }
        user.addRole(role, roleRepository);
        eventService.log(
                Action.GRANT_ROLE,
                "Grant role " + role + " to " + user.getUsername(),
                subjectUser
        );
        return mapper.userToGetUserDto(userRepository.save(user));
    }

    private GetUserDto removeRole(Role role, String username, User subjectUser) {
        User user = loadUserByUsername(username);
        Validators.validateRemoveUserRole(user.getRoles(), role);
        user.removeRole(role);
        eventService.log(
                Action.REMOVE_ROLE,
                "Remove role " + role + " from " + username,
                subjectUser
        );
        return mapper.userToGetUserDto(userRepository.save(user));
    }

    @Transactional
    public ResponseEntity<?> deleteUser(String username, User subjectUser) {
        User user = loadUserByUsername(username);
        if (user.getRoles().contains(Role.ADMINISTRATOR)) {
            throw new ValidException("Can't remove ADMINISTRATOR role!");
        }
        log.info("Deleting user \"" + username + "\"");
        userRepository.deleteByUsername(username);
        eventService.log(Action.DELETE_USER, username, subjectUser);
        return ResponseStatus.builder()
                .add("status", "Deleted successfully!")
                .add("user", username)
                .build();
    }

    public ResponseEntity<?> lockOrUnlock(UpdateLockUserDto dto,  User subjectUser) {
        UpdateLockUserDto.Operation operation =
                AppUtils.valueOf(UpdateLockUserDto.Operation.class, dto.getOperation());
        switch (operation) {
            case LOCK:
                lock(dto.getUser(), subjectUser);
                return ResponseStatus.builder()
                        .add("status", "User " + dto.getUser() + " locked!")
                        .build();
            case UNLOCK:
                unlock(dto.getUser(), subjectUser);
                return ResponseStatus.builder()
                        .add("status", "User " + dto.getUser() + " unlocked!")
                        .build();
            default:
                throw new IllegalStateException();
        }
    }

    public void lock(String username, User subjectUser) {
        User user = loadUserByUsername(username);
        if (user.getRoles().contains(Role.ADMINISTRATOR)) {
            throw new ValidException("Can't lock the ADMINISTRATOR!");
        }
        user.setAccountNonLocked(false);
        userRepository.save(user);
        eventService.log(Action.LOCK_USER, "Lock user " + user.getUsername(), subjectUser);
    }

    public void unlock(String username, User subjectUser) {
        User user = loadUserByUsername(username);
        user.setAccountNonLocked(true);
        userRepository.save(user);
        eventService.log(Action.UNLOCK_USER, "Unlock user " + user.getUsername(), subjectUser);
    }

}
