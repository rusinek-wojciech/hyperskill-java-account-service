package account.service;

import account.dto.DeleteUserStatusDto;
import account.dto.UserGetDto;
import account.mapper.Mapper;
import account.model.User;
import account.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    private final Mapper mapper;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public List<UserGetDto> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(mapper::userToUserGetDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeleteUserStatusDto deleteUser(String username) {
        boolean isNotDeleted = userRepository.deleteByUsername(username) == 0L;
        if (isNotDeleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + username + " does not exist");
        }
        return DeleteUserStatusDto.builder()
                .user(username)
                .status("Deleted successfully!")
                .build();
    }

}
