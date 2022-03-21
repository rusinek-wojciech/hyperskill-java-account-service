package account.controller;

import account.dto.user.UpdatePasswordUserDto;
import account.dto.user.CreateUserDto;
import account.dto.user.GetUserDto;
import account.model.user.User;
import account.service.AuthService;
import javax.validation.Valid;

import account.util.ResponseStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("signup")
    public GetUserDto register(@RequestBody @Valid CreateUserDto dto) {
        return authService.register(dto);
    }

    @PostMapping("changepass")
    public ResponseEntity<?> changePassword(@RequestBody @Valid UpdatePasswordUserDto dto,
                                            @AuthenticationPrincipal User user) {
        User changedUser = authService.changePassword(user, dto.getNewPassword());
        return ResponseStatus.builder()
                .add("email", changedUser.getEmail())
                .add("status", "The password has been updated successfully")
                .build();
    }

    @GetMapping("login")
    public GetUserDto login(@AuthenticationPrincipal User user) {
        return authService.getCurrentUser(user);
    }

}
