package account.controller;

import account.dto.user.GetUserDto;
import account.dto.user.UpdateRoleUserDto;
import account.model.user.Role;
import account.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;

    @PutMapping("user/role")
    @Secured({Role.ROLE_ADMINISTRATOR})
    public GetUserDto updateUserRole(@RequestBody UpdateRoleUserDto updateRoleUserDto) {
        return userService.changeUserRole(updateRoleUserDto);
    }

    @DeleteMapping("user/{username}")
    @Secured({Role.ROLE_ADMINISTRATOR})
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @GetMapping("user")
    @Secured({Role.ROLE_ADMINISTRATOR})
    public List<GetUserDto> getUser() {
        return userService.getAllUsers();
    }

}
