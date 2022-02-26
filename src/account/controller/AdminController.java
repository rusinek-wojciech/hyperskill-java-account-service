package account.controller;

import account.dto.user.GetUserDto;
import account.dto.user.UpdateLockUserDto;
import account.dto.user.UpdateRoleUserDto;
import account.model.user.User;
import account.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;

    @PutMapping("user/role")
    public GetUserDto updateUserRole(@Valid @RequestBody UpdateRoleUserDto updateRoleUserDto,
                                     @AuthenticationPrincipal User user) {
        return userService.changeUserRole(updateRoleUserDto, user);
    }

    @DeleteMapping("user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username,
                                        @AuthenticationPrincipal User user) {
        return userService.deleteUser(username, user);
    }

    @GetMapping("user")
    public List<GetUserDto> getUser() {
        return userService.getAllUsers();
    }

    // block after 5 fails, log LOGIN_FAILED
    @PutMapping("user/access")
    public ResponseEntity<?> lockOrUnlock(@Valid @RequestBody UpdateLockUserDto updateLockUserDto,
                                          @AuthenticationPrincipal User user) {
        return userService.lockOrUnlock(updateLockUserDto, user);
    }

}
