package account.controller;

import account.dto.user.UpdatePasswordUserDto;
import account.dto.user.CreateUserDto;
import account.dto.user.GetUserDto;
import account.model.user.Role;
import account.model.user.User;
import account.service.AuthService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("signup")
    public GetUserDto register(@RequestBody @Valid CreateUserDto createUserDto) {
        return authService.register(createUserDto);
    }

    @PostMapping("changepass")
    @Secured({Role.ROLE_USER, Role.ROLE_ACCOUNTANT, Role.ROLE_ADMINISTRATOR})
    public ResponseEntity<?> changePassword(@RequestBody @Valid UpdatePasswordUserDto updatePasswordUserDto,
                                            @AuthenticationPrincipal User user) {
        return authService.changePassword(user, updatePasswordUserDto.getNewPassword());
    }

}
