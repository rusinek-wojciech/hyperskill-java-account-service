package account.service;

import account.dto.user.GetUserDto;
import account.dto.user.UpdateRoleUserDto;
import account.mapper.Mapper;
import account.model.User;
import account.repository.RoleRepository;
import account.repository.UserRepository;
import account.util.ResponseStatus;
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

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + username + " does not exist"));
    }

    public List<GetUserDto> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(mapper::userToGetUserDto)
                .collect(Collectors.toList());
    }

    public GetUserDto changeUserRole(UpdateRoleUserDto dto) {
        User user = loadUserByUsername(dto.getUser());
        if (dto.getOperation() == UpdateRoleUserDto.Operation.GRANT) {
            user.addRole(dto.getRole(), roleRepository);
        } else if (dto.getOperation() == UpdateRoleUserDto.Operation.REMOVE) {
            user.removeRole(dto.getRole());
        }
        return mapper.userToGetUserDto(userRepository.save(user));
    }

    @Transactional
    public ResponseEntity<?> deleteUser(String username) {
        boolean isNotDeleted = userRepository.deleteByUsername(username) == 0L;
        if (isNotDeleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + username + " does not exist");
        }
        return ResponseStatus.builder()
                .add("status", "Deleted successfully!")
                .add("user", username)
                .build();
    }

}
