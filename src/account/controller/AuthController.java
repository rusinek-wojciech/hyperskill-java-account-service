package account.controller;

import account.config.AuthFacade;
import account.dto.PasswordStatusDto;
import account.dto.PasswordUpdateDto;
import account.dto.UserCreateDto;
import account.dto.UserGetDto;
import account.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthFacade authFacade;

    @PostMapping("api/auth/signup")
    public UserGetDto signUp(@RequestBody @Valid UserCreateDto userCreateDto) {
        return userService.signUp(userCreateDto);
    }

    @PostMapping("api/auth/changepass")
    public PasswordStatusDto changePassword(@RequestBody @Valid PasswordUpdateDto passwordUpdateDto) {
        return userService.changePassword(
                passwordUpdateDto.getNewPassword(),
                authFacade.getAuth().getName()
        );
    }

}
