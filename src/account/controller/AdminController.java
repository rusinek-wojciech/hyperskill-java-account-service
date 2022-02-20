package account.controller;

import account.dto.user.GetUserDto;
import account.dto.user.UpdateRoleUserDto;
import account.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;

    @PutMapping("user/role")
    public GetUserDto updateUserRole(@Valid @RequestBody UpdateRoleUserDto updateRoleUserDto) {
        return userService.changeUserRole(updateRoleUserDto);
    }

    @DeleteMapping("user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @GetMapping("user")
    public List<GetUserDto> getUser() {
        return userService.getAllUsers();
    }

}
