package account.controller;

import account.dto.PasswordStatusDto;
import account.dto.PasswordUpdateDto;
import account.dto.UserCreateDto;
import account.dto.UserGetDto;
import account.service.AuthService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("signup")
    public UserGetDto signUp(@RequestBody @Valid UserCreateDto userCreateDto) {
        return authService.signUp(userCreateDto);
    }

    @PostMapping("changepass")
    public PasswordStatusDto changePassword(@RequestBody @Valid PasswordUpdateDto passwordUpdateDto) {
        return authService.changePassword(passwordUpdateDto);
    }

}
