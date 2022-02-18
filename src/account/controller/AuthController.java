package account.controller;

import account.dto.user.UpdatePasswordUserDto;
import account.dto.user.CreateUserDto;
import account.dto.user.GetUserDto;
import account.model.user.User;
import account.service.AuthService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public GetUserDto signUp(@RequestBody @Valid CreateUserDto createUserDto) {
        return authService.signUp(createUserDto);
    }

    @PostMapping("changepass")
    public ResponseEntity<?> changePassword(@RequestBody @Valid UpdatePasswordUserDto updatePasswordUserDto,
                                            @AuthenticationPrincipal User user) {
        return authService.changePassword(user, updatePasswordUserDto.getNewPassword());
    }

}
