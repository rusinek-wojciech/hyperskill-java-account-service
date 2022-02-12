package account.mapper;

import account.dto.UserCreateDto;
import account.dto.UserGetDto;
import account.model.User;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public UserGetDto userToUserGetDto(User user) {
        return UserGetDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getUsername())
                .build();
    }

    public User userCreateDtoToUser(UserCreateDto userCreateDto) {
        return new User(
                userCreateDto.getName(),
                userCreateDto.getLastname(),
                userCreateDto.getEmail().toLowerCase(),
                userCreateDto.getPassword()
        );
    }

}
